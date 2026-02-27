package com.customization.yll.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.exception.SqlExecuteException;
import weaver.conn.RecordSet;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author yaolilin
 * @desc 表单工具类
 * @date 2024/8/26
 **/
public class FormUtil {
    private FormUtil() {
    }

    /**
     * 获取字段的显示名称
     *
     * @param fieldId    字段id
     * @param workflowId 流程id
     * @param recordSet  recordSet
     * @return 字段的显示名称
     */
    public static String getFieldName(int fieldId, int workflowId, RecordSet recordSet) {
        return FieldUtil.getWorkflowFieldName(fieldId, workflowId, recordSet);
    }

    /**
     * 根据流程字段数据库名，获取流程字段的显示名称
     * @param fieldDbName 流程字段数据库名
     * @param workflowId 流程id
     * @param detailTableName 如果字段为明细表，需要传入明细表名，否则为null即可
     * @param recordSet recordSet
     * @return 流程字段的显示名称
     */
    public static String getFieldName(String fieldDbName, int workflowId, String detailTableName, RecordSet recordSet) {
        return FieldUtil.getWorkflowFieldName(fieldDbName, workflowId, detailTableName, recordSet);
    }

    /**
     * 获取表单表名称
     * @param billId 表单id
     * @param recordSet recordSet
     * @return 表单表名称
     */
    public static String getFormTableName(int billId, RecordSet recordSet) {
        recordSet.executeQuery("select tablename from workflow_bill where id=?", billId);
        recordSet.next();
        return recordSet.getString("tablename");
    }

    /**
     * 根据表名获取表单id
     *
     * @param tableName 表名
     * @param recordSet RecordSet
     * @return 表单id
     */
    public static Optional<Integer> getFormIdByTableName(String tableName,RecordSet recordSet) {
        if (StrUtil.isBlank(tableName)) {
            return Optional.empty();
        }
        String sql = "select id from workflow_bill where tablename = ?";
        if (!recordSet.executeQuery(sql, tableName)) {
            throw new SqlExecuteException(buildSqlMessage(sql, tableName), sql);
        }
        if (!recordSet.next()) {
            return Optional.empty();
        }
        Integer id = Convert.toInt(recordSet.getString("id"));
        if (id == null) {
            return Optional.empty();
        }
        return Optional.of(id);
    }

    private static String buildSqlMessage(String sql, Object... params) {
        return "执行sql失败，sql: " + sql + "，params: " + Arrays.toString(params);
    }

}
