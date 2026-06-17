package com.customization.yll.common.util;

import com.customization.yll.common.IntegrationLog;
import com.customization.yll.common.exception.SqlExecuteException;
import weaver.conn.RecordSet;
import weaver.general.Util;

/**
 * 基于 MySQL GET_LOCK/RELEASE_LOCK 的分布式锁<br>
 * <p>使用 MySQL 会话级命名锁实现跨服务器的并发控制，适用于流水号生成等需要互斥操作的场景。<br>
 * 注意：该实现仅适用于 <b>MySQL 数据库</b>，其他数据库（如达梦、Oracle）不支持 {@code GET_LOCK()} 函数。</p>
 * <p>使用示例：</p>
 * <pre>{@code
 * try (MysqlDbLock lock = new MysqlDbLock("lock_formtable_main_xx", 30)) {
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
public class MysqlDbLock implements AutoCloseable {

    private static final IntegrationLog log = new IntegrationLog(MysqlDbLock.class);

    /**
     * 默认锁超时时间（秒）
     */
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private final RecordSet recordSet;
    private final String lockName;
    private final boolean locked;

    /**
     * 尝试获取锁
     *
     * @param lockName       锁名称，建议以业务标识为前缀，如 {@code lock_formtable_main_xx}
     * @param timeoutSeconds 等待锁的超时时间（秒），超时仍未获取到锁则返回 false
     */
    public MysqlDbLock(String lockName, int timeoutSeconds) {
        this.recordSet = new RecordSet();
        this.lockName = lockName;
        this.locked = acquire(timeoutSeconds);
    }

    /**
     * 尝试获取锁，默认超时 30 秒
     *
     * @param lockName 锁名称
     */
    public MysqlDbLock(String lockName) {
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
     * 释放锁
     */
    @Override
    public void close() {
        if (locked) {
            recordSet.execute("SELECT RELEASE_LOCK('" + lockName + "')");
            log.info("释放锁，lockName: {}", lockName);
        }
    }

    /**
     * 获取锁，使用 {@code GET_LOCK()} 函数
     *
     * @param timeoutSeconds 超时秒数
     * @return true 获取成功，false 获取超时
     */
    private boolean acquire(int timeoutSeconds) {
        String sql = "SELECT GET_LOCK('" + lockName + "', " + timeoutSeconds + ") AS result";
        if (!recordSet.execute(sql)) {
            throw new SqlExecuteException("获取锁失败，lockName: " + lockName);
        }
        recordSet.next();
        int result = Util.getIntValue(recordSet.getString("result"), 0);
        if (result == 1) {
            log.info("获取锁成功，lockName: {}", lockName);
            return true;
        } else {
            log.warn("获取锁超时，lockName: {}, timeout: {}s", lockName, timeoutSeconds);
            return false;
        }
    }
}
