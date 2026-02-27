package com.customization.yll.common.util;

import cn.hutool.core.collection.CollUtil;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetExecutionInterface;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 数据库执行工具
 * @date 2024/6/14
 */
@UtilityClass
public class DbUtil {
    private static final Logger logger = LoggerFactory.getLogger(DbUtil.class);

    /**
     * 插入数据
     * @param tableName 表名
     * @param fieldData 插入数据
     * @param recordSet recordSet
     * @return 是否成功
     */
    public static boolean insert(String tableName, Map<String, Object> fieldData, RecordSet recordSet) {
        try {
            return insertByRsInterface(tableName, fieldData, recordSet);
        } catch (Exception e) {
            logger.error("插入数据发生异常", e);
            return false;
        }
    }

    /**
     * 插入数据，可用于执行事务
     *
     * @param tableName 表名
     * @param fieldData 插入数据
     * @param recordSet recordSet
     * @return 是否成功
     * @throws Exception 数据库执行发生异常
     */
    public static boolean insertByRsInterface(String tableName, Map<String, Object> fieldData,
                                              RecordSetExecutionInterface recordSet) throws Exception {
        String  sql = SqlUtil.buildInsertSql(tableName, new ArrayList<>(fieldData.keySet()));
        List<Object> insertData = new ArrayList<>(fieldData.values());
        logger.info("sql:" + sql);
        return recordSet.executeSql(sql, false, "", false, insertData.toArray());
    }

    /**
     * 更新数据
     * @param data 更新数据，字段名与更新的值映射，也就是sql的set部分
     * @param conditions 条件，不能为空，都为相等条件，如 name=? and age=?
     * @param tableName 表名
     * @param recordSet recordSet
     * @return 是否成功
     */
    public static boolean update(Map<String, Object> data, Map<String ,Object> conditions,
                              String tableName, RecordSet recordSet) {
        if (CollUtil.isEmpty(conditions)) {
            logger.error("更新错误，更新条件不能为空");
            return false;
        }
        List<Object> values = new ArrayList<>(data.values());
        String sql = buildUpdateSql(data, conditions, tableName, values);
        logger.info("sql:" + sql);
        return recordSet.executeUpdate(sql, values);
    }

    /**
     * 大批量数据更新
     * @param updateDataList 每条更新数据的字段名与更新的值映射，也就是sql的set部分
     * @param conditionList 每条数据的更新条件，字段名与条件值的值映射，不能为空，都为相等条件，如 name=? and age=?。
     *                      元素顺序必需与 updateDataList 中的元素顺序一致，且 list 大小与 updateDataList 相等
     * @param tableName 更新表名
     * @return 是否成功
     */
    public static boolean batchUpdate(List<Map<String, Object>> updateDataList,
                                      List<Map<String, Object>> conditionList,
                                      @NotNull String tableName){
        if (CollUtil.isEmpty(updateDataList)) {
            logger.info("无更新数据，不进行操作");
            return true;
        }
        if (CollUtil.isEmpty(conditionList)) {
            logger.error("更新错误，更新条件不能为空");
            return false;
        }
        if (updateDataList.size() != conditionList.size()) {
            logger.error("更新错误，更新条件数量与更新数据数量不一致");
            return false;
        }
        RecordSet recordSet = new RecordSet();
        List<List> values = new ArrayList<>();
        String sql = null;
        for (int i = 0; i < updateDataList.size(); i++) {
            Map<String, Object> updateData = updateDataList.get(i);
            List<Object> valuesItem = new ArrayList<>(updateData.values());
            Map<String, Object> condition = conditionList.get(i);
            if (sql == null) {
                sql = buildUpdateSql(updateData, condition, tableName, valuesItem);
            }else {
                // 插入条件的值到占位符值列表中
                valuesItem.addAll(condition.values());
            }
            values.add(valuesItem);
        }
        logger.info("sql:" + sql);
        return recordSet.executeBatchSql(sql, values);
    }

    /**
     * 批量插入数据到指定表中
     * @param data 要插入的数据列表，每个元素是一个Map，key为字段名，value为字段值
     * @param tableName 目标表名，不能为空
     * @return 插入成功返回true，失败返回false
     */
    public static boolean batchInsert(List<Map<String, Object>> data, @NotNull String tableName) {
        try {
            return batchInsert(data, tableName, null);
        } catch (Exception e) {
            logger.error("批量插入数据发生异常", e);
            return false;
        }
    }

    /**
     * 批量插入数据到指定表中，可用于执行事务
     *
     * @param data      要插入的数据列表，每个元素是一个Map，key为字段名，value为字段值
     * @param tableName 目标表名，不能为空
     * @param recordSet 批量插入的RecordSet对象，如果为null，则创建一个新的RecordSet对象
     * @return 插入成功返回true，失败返回false
     * @throws Exception 数据库执行发生异常
     */
    public static boolean batchInsert(List<Map<String, Object>> data, @NotNull String tableName,
                                      @Nullable RecordSetExecutionInterface recordSet) throws Exception {
        if (CollUtil.isEmpty(data)) {
            logger.info("无插入数据，不进行操作");
            return true;
        }
        if (recordSet == null) {
            recordSet = new RecordSet();
        }
        List<List<Object>> values = new ArrayList<>();
        String sql = null;
        for (Map<String, Object> item : data) {
            List<Object> valuesItem = new ArrayList<>(item.values());
            if (sql == null) {
                sql = SqlUtil.buildInsertSql(tableName, new ArrayList<>(item.keySet()));
            }
            values.add(valuesItem);
        }
        logger.info("sql:" + sql);
        int[] results = recordSet.executeBatchSql_proxy(sql, values, "");
        if (results != null) {
            logger.debug("批量插入返回结果：" + Arrays.toString(results));
        }
        return results != null && results.length > 0;
    }

    @NotNull
    private static String buildUpdateSql(Map<String, Object> data, Map<String, Object> conditions,
                                         String tableName, List<Object> values) {
        String sql = SqlUtil.buildUpdateSql(tableName, new ArrayList<>(data.keySet()));
        String whereSql = SqlUtil.buildEqualsWhere(conditions, values);
        sql += whereSql;
        return sql;
    }
}
