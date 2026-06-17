package com.customization.yll.common.util;

import com.customization.yll.common.IntegrationLog;
import weaver.conn.RecordSet;
import java.net.InetAddress;

/**
 * 基于数据库表的分布式锁实现<br>
 * <p>使用专用锁表 {@code pub_lock} 实现分布式锁，兼容达梦、MySQL 等主流数据库。<br>
 * 不再依赖 MySQL 的 {@code GET_LOCK()}/{@code RELEASE_LOCK()} 函数。</p>
 * <p><b>锁获取原理：</b></p>
 * <ul>
 *   <li>向锁表插入一条记录（lock_name 为主键），成功则获取锁</li>
 *   <li>插入失败（主键冲突）则说明锁被其他线程持有，等待后重试</li>
 *   <li>锁记录包含锁定时间戳，超时后自动过期，防止因 JVM 崩溃等原因导致死锁</li>
 * </ul>
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * try (DbLock lock = new DbLock("lock_formtable_main_xx", 30)) {
 *     if (!lock.isLocked()) {
 *         // 处理获取锁超时
 *         return;
 *     }
 *     // 执行互斥业务逻辑
 * }
 * }</pre>
 *
 * @author 姚礼林
 * @date 2026/6/3
 */
public class DbLock implements AutoCloseable {

    private static final IntegrationLog log = new IntegrationLog(DbLock.class);

    /**
     * 默认锁超时时间（秒）
     */
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    /**
     * 锁记录自动过期时间（毫秒），防止 JVM 崩溃后锁一直无法释放
     */
    private static final long LOCK_EXPIRE_MS = 120_000;

    /**
     * 锁表名称
     */
    private static final String LOCK_TABLE_NAME = "pub_lock";

    /**
     * 锁表是否已初始化（尝试创建过）
     */
    private static volatile boolean tableInitialized = false;

    private final RecordSet recordSet;
    private final String lockName;
    private final boolean locked;
    private final String lockedBy;

    /**
     * 尝试获取锁
     *
     * @param lockName       锁名称，建议以业务标识为前缀，如 {@code lock_formtable_main_xx}
     * @param timeoutSeconds 等待锁的超时时间（秒），超时仍未获取到锁则返回 false
     */
    public DbLock(String lockName, int timeoutSeconds) {
        this.recordSet = new RecordSet();
        this.lockName = lockName;
        this.lockedBy = buildLockedBy();
        initLockTable();
        this.locked = acquire(timeoutSeconds);
    }

    /**
     * 尝试获取锁，默认超时 30 秒
     *
     * @param lockName 锁名称
     */
    public DbLock(String lockName) {
        this(lockName, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 获取锁是否成功
     *
     * @return true 表示成功获取到锁
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * 释放锁，删除锁表中的对应记录
     */
    @Override
    public void close() {
        if (locked) {
            String sql = "DELETE FROM " + LOCK_TABLE_NAME + " WHERE lock_name = '" + lockName + "'";
            if (recordSet.execute(sql)) {
                log.info("释放锁，lockName: {}", lockName);
            } else {
                log.warn("释放锁失败，lockName: {}", lockName);
            }
        }
    }

    /**
     * 获取锁，通过 INSERT 方式尝试获取，主键冲突则等待重试
     *
     * @param timeoutSeconds 超时秒数
     * @return true 获取成功，false 获取超时
     */
    private boolean acquire(int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        long retryInterval = 200L;

        while (System.currentTimeMillis() < deadline) {
            cleanExpiredLocks();
            if (tryInsertLock()) {
                log.info("获取锁成功，lockName: {}, lockedBy: {}", lockName, lockedBy);
                return true;
            }
            sleep(retryInterval);
        }

        log.warn("获取锁超时，lockName: {}, timeout: {}s", lockName, timeoutSeconds);
        return false;
    }

    /**
     * 尝试向锁表插入记录，插入成功表示获取锁
     *
     * @return true 插入成功（获取锁），false 插入失败（锁已被持有）
     */
    private boolean tryInsertLock() {
        long now = System.currentTimeMillis();
        String sql = "INSERT INTO " + LOCK_TABLE_NAME + "(lock_name, locked_at, locked_by) "
                + "VALUES('" + lockName + "', " + now + ", '" + lockedBy + "')";
        try {
            return recordSet.execute(sql);
        } catch (Exception e) {
            // 主键冲突或其它异常，表示锁已被其它线程持有
            return false;
        }
    }

    /**
     * 清理已过期的锁记录，防止死锁
     */
    private void cleanExpiredLocks() {
        long expireThreshold = System.currentTimeMillis() - LOCK_EXPIRE_MS;
        String sql = "DELETE FROM " + LOCK_TABLE_NAME + " WHERE locked_at < " + expireThreshold;
        try {
            if (!recordSet.execute(sql)) {
                log.warn("清理过期锁失败，lockName: {}", lockName);
            }
        } catch (Exception e) {
            log.warn("清理过期锁异常，lockName: {}", lockName, e);
        }
    }

    /**
     * 初始化锁表，尝试创建表（如果已存在则忽略）
     */
    private static void initLockTable() {
        if (tableInitialized) {
            return;
        }
        synchronized (DbLock.class) {
            if (tableInitialized) {
                return;
            }
            String sql = "CREATE TABLE " + LOCK_TABLE_NAME + " ("
                    + "lock_name VARCHAR(200) NOT NULL PRIMARY KEY, "
                    + "locked_at NUMERIC(20) NOT NULL, "
                    + "locked_by VARCHAR(100))";
            try {
                RecordSet rs = new RecordSet();
                if (!rs.execute(sql)) {
                    log.info("锁表已存在，跳过创建，tableName: {}", LOCK_TABLE_NAME);
                    tableInitialized = true;
                    return;
                }
                log.info("锁表创建成功，tableName: {}", LOCK_TABLE_NAME);
            } catch (Exception e) {
                log.info("锁表已存在，跳过创建，tableName: {}", LOCK_TABLE_NAME);
            }
            tableInitialized = true;
        }
    }

    /**
     * 构建锁定者标识，用于排查问题
     *
     * @return 主机名-线程名 格式的字符串
     */
    private static String buildLockedBy() {
        try {
            return InetAddress.getLocalHost().getHostName() + "-" + Thread.currentThread().getName();
        } catch (Exception e) {
            return Thread.currentThread().getName();
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
