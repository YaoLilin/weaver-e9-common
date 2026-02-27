package com.customization.yll.common.util;

import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.exception.SqlExecuteException;
import com.customization.yll.common.IntegrationLog;
import com.customization.yll.common.constants.FieldType;
import lombok.experimental.UtilityClass;
import weaver.conn.RecordSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author yaolilin
 * @desc 表单字段工具类
 * @date 2025/2/11
 **/
@UtilityClass
public class FieldUtil {
    private static final IntegrationLog log = new IntegrationLog(FieldUtil.class);

    /**
     * 根据字段id获取字段类型
     * @param fieldId 字段id
     * @param recordSet recordSet
     * @return 字段类型，如果找不到字段类型则返回 Optional.empty()。
     */
    public static Optional<FieldType> getFieldType(int fieldId, RecordSet recordSet) {
        recordSet.executeQuery("select fieldhtmltype,selectitemtype,type from workflow_billfield " +
                "where id=?", fieldId);
        recordSet.next();
        String htmlType = recordSet.getString("fieldhtmltype");
        String selectItemType = recordSet.getString("selectitemtype");
        String type = recordSet.getString("type");
        if ("5".equals(htmlType)) {
            // 选择框类型
            if ("1".equals(selectItemType)) {
                return Optional.of(FieldType.PUBLIC_SELECTOR);
            }
            return Optional.of(FieldType.OWN_SELECTOR);
        } else if ("3".equals(htmlType)) {
            // 浏览框类型
            if ("161".equals(type)) {
                return Optional.of(FieldType.CUSTOM_MODE_BROWSER);
            } else if ("1".equals(type)) {
                return Optional.of(FieldType.SINGLE_HRM_BROWSER);
            } else if ("17".equals(type)) {
                return Optional.of(FieldType.MULTI_HRM_BROWSER);
            } else if ("4".equals(type)) {
                return Optional.of(FieldType.SINGLE_DEPARTMENT_BROWSER);
            } else if ("18".equals(type)) {
                return Optional.of(FieldType.MULTI_DEPARTMENT_BROWSER);
            } else if ("9".equals(type)) {
                return Optional.of(FieldType.SINGLE_DOC_BROWSER);
            } else if ("37".equals(type)) {
                return Optional.of(FieldType.MULTI_DOC_BROWSER);
            }
        }
        return Optional.empty();
    }

    /**
     * 根据字段名称获取字段id
     * @param fieldName 字段名称
     * @param formId 表单id，对应 workflow_bill 表的id
     * @param detailTableName 如果字段是明细表，则需要填写明细表名
     * @param recordSet recordSet
     * @return 字段id，如果获取不到会返回 -1
     */
    public static int getFieldId(String fieldName,int formId, String detailTableName,RecordSet recordSet) {
        if (StrUtil.isNotEmpty(detailTableName)) {
            recordSet.executeQuery("select id from workflow_billfield where fieldname=? and billid=? and " +
                            "detailtable=?", fieldName, formId,detailTableName);
        }else {
            recordSet.executeQuery("select id from workflow_billfield where fieldname=? and billid=? and " +
                    "detailtable=''", fieldName, formId);
        }
        recordSet.next();
        return recordSet.getInt("id");
    }

    /**
     * 获取下拉框字段的选项显示名称
     * @param tableName 流程表名
     * @param fieldName 下拉框字段数据库名称
     * @param selectValue 下拉框选项id（值）
     * @param recordSet recordSet
     * @return 下拉框字段的选项显示名称
     */
    public static String getSelectItemShowName(String tableName, String fieldName, int selectValue, RecordSet recordSet){
        return getSelectItemShowName(tableName,null,fieldName,selectValue,recordSet);
    }

    /**
     * 获取下拉框字段的选项显示名称
     * @param tableName 流程表名
     * @param detailTableName 明细表名,如何是明细字段请填写
     * @param fieldName 下拉框字段数据库名称
     * @param selectValue 下拉框选项id（值）
     * @param recordSet recordSet
     * @return 下拉框字段的选项显示名称
     */
    public static String getSelectItemShowName(String tableName, String detailTableName,String fieldName,
                                               int selectValue, RecordSet recordSet){
        if (StrUtil.isNotEmpty(detailTableName)) {
            recordSet.executeQuery("SELECT s.SELECTNAME FROM workflow_selectitem s,workflow_bill b,workflow_billfield f " +
                    "WHERE s.SELECTVALUE=? AND s.fieldid=f.id AND f.detailtable=? AND f.fieldname=? " +
                    "AND f.billid=b.id AND b.tablename=? ",selectValue,detailTableName,fieldName,tableName);
        }else {
            recordSet.executeQuery("SELECT s.SELECTNAME FROM workflow_selectitem s,workflow_bill b,workflow_billfield f " +
                    "WHERE s.SELECTVALUE=? AND s.fieldid=f.id AND (f.detailtable='' OR f.detailtable IS NULL) " +
                    "AND f.fieldname=? " +
                    "AND f.billid=b.id AND b.tablename=? ",selectValue,fieldName,tableName);
        }
        recordSet.next();
        return recordSet.getString("SELECTNAME");
    }

    /**
     * 获取下拉框字段的选项显示名称
     * @param fieldId 下拉框字段id，id不需要加上“field”,只需要后面的数字
     * @param selectValue 下拉框选项id（值）
     * @param recordSet recordSet
     * @return 下拉框字段的选项显示名称
     */
    public static String getSelectItemShowName(int fieldId, int selectValue, RecordSet recordSet){
        recordSet.executeQuery("SELECT s.SELECTNAME from workflow_selectitem s WHERE s.SELECTVALUE=? AND s.fieldid=? "
                ,selectValue,fieldId);
        recordSet.next();
        return recordSet.getString("SELECTNAME");
    }

    /**
     * 获取公共选择框的选项显示名称
     * @param fieldId 字段id，对应 workflow_billfield 表的id
     * @param selectValue 字段值
     * @param recordSet recordSet
     * @return 公共选择框的选项显示名称
     */
    public static String getPublicSelectorShowName(int fieldId, int selectValue, RecordSet recordSet) {
        recordSet.executeQuery("select pubchoiceid from workflow_billfield where id=?", fieldId);
        recordSet.next();
        String selectorId = recordSet.getString("pubchoiceid");
        if (StrUtil.isEmpty(selectorId)) {
            log.error("找不到公共选择框id，fieldId："+fieldId);
            return "";
        }
        recordSet.executeQuery("SELECT name from mode_selectitempagedetail where mainid=? ORDER BY id ASC",
                selectorId);
        int index = 0;
        while (recordSet.next()) {
            if (index == selectValue) {
                return recordSet.getString("name");
            }
            index++;
        }
        log.error("匹配不到公共选择框的选项，fieldId："+fieldId);
        return "";
    }

    /**
     * 获取字段的显示名称
     *
     * @param fieldId    字段id
     * @param workflowId 流程id
     * @param recordSet  recordSet
     * @return 字段的显示名称
     */
    public static String getWorkflowFieldName(int fieldId, int workflowId, RecordSet recordSet) {
        if (fieldId < 1) {
            return "";
        }
        recordSet.executeQuery("SELECT a.fieldname FROM workflow_billfield a INNER JOIN workflow_base b " +
                "ON a.billid=b.formid WHERE b.id=? AND b.isbill='1' AND a.id=?", workflowId, fieldId);
        recordSet.next();
        return recordSet.getString("fieldname");
    }

    /**
     * 根据流程字段数据库名，获取流程字段的显示名称
     * @param fieldDbName 流程字段数据库名
     * @param workflowId 流程id
     * @param detailTableName 如果字段为明细表，需要传入明细表名，否则为null即可
     * @param recordSet recordSet
     * @return 流程字段的显示名称
     */
    public static String getWorkflowFieldName(String fieldDbName, int workflowId, String detailTableName, RecordSet recordSet) {
        if (detailTableName == null) {
            detailTableName = "";
        }
        String sql = "SELECT h.labelname FROM workflow_billfield a "
                + "INNER JOIN workflow_base b ON a.billid=b.formid JOIN htmllabelinfo h ON a.fieldlabel=h.indexid "
                + "AND h.languageid=7 WHERE b.id=? AND b.isbill='1' AND a.fieldname=? AND a.detailtable=?";
        if (!recordSet.executeQuery(sql, workflowId, fieldDbName, detailTableName)) {
            throw new SqlExecuteException("执行sql失败，sql: " + sql + "，params: ["
                    + workflowId + ", " + fieldDbName + ", " + detailTableName + "]", sql);
        }
        recordSet.next();
        return recordSet.getString("labelname");
    }

    /**
     * 根据表单字段数据库名获取显示名称
     *
     * @param fieldDbName 字段数据库名
     * @param formId 表单id（workflow_bill.id）
     * @param detailTableName 明细表名，为主表字段时传空字符串或null
     * @param recordSet recordSet
     * @return 字段显示名称
     */
    public static String getFormFieldName(String fieldDbName, int formId, String detailTableName, RecordSet recordSet) {
        if (detailTableName == null) {
            detailTableName = "";
        }
        String sql = "SELECT h.labelname FROM workflow_billfield a "
                + "JOIN htmllabelinfo h ON a.fieldlabel=h.indexid AND h.languageid=7 "
                + "WHERE a.billid=? AND a.fieldname=? ";
        List<Object> params = new ArrayList<>();
        params.add(formId);
        params.add(fieldDbName);
        String detailTableCondition;
        if (StrUtil.isNotBlank(detailTableName)) {
            detailTableCondition = " AND a.detailtable=?";
            params.add(detailTableName);
        }else {
            detailTableCondition = " AND (a.detailtable is null OR a.detailtable = '')";
        }
        sql += detailTableCondition;
        if (!recordSet.executeQuery(sql, params.toArray())) {
            throw new SqlExecuteException("执行sql失败，sql: " + sql + "，params: ["
                    + formId + ", " + fieldDbName + ", " + detailTableName + "]", sql);
        }
        recordSet.next();
        return recordSet.getString("labelname");
    }
}
