package com.customization.yll.common;

import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.constants.FieldType;
import com.customization.yll.common.util.HrmInfoUtil;
import com.customization.yll.common.util.WorkflowUtil;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author yaolilin
 * @desc 获取表单字段的显示值，比如获取人力资源字段的显示值，支持的字段类型详见 {@link FieldType}
 * @date 2025/2/10
 **/
public class FieldShowValueConverter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public String convert(int fieldId, String fieldValue, FieldType fieldType) {
        if (StrUtil.isEmpty(fieldValue)) {
            return fieldValue;
        }
        RecordSet recordSet = new RecordSet();
        switch (fieldType) {
            case OWN_SELECTOR:
                if (fieldValue.contains(",")) {
                    return Arrays.stream(fieldValue.split(",")).map(i ->
                            WorkflowUtil.getSelectItemShowName( fieldId,
                                    Integer.parseInt(i), recordSet)).collect(Collectors.joining(","));
                }
                return WorkflowUtil.getSelectItemShowName(fieldId, Integer.parseInt(fieldValue), recordSet);
            case PUBLIC_SELECTOR:
                if (fieldValue.contains(",")) {
                    return Arrays.stream(fieldValue.split(",")).map(i ->
                            WorkflowUtil.getPublicSelectorShowName(fieldId,
                                    Integer.parseInt(i), recordSet)).collect(Collectors.joining(","));
                }
                return WorkflowUtil.getPublicSelectorShowName(fieldId, Integer.parseInt(fieldValue), recordSet);
            case SINGLE_HRM_BROWSER:
                return HrmInfoUtil.getLastName(Integer.valueOf(fieldValue), recordSet);
            case MULTI_HRM_BROWSER:
                return Arrays.stream(fieldValue.split(",")).map(i ->
                                HrmInfoUtil.getLastName(Integer.valueOf(i), recordSet))
                        .collect(Collectors.joining(","));
            case SINGLE_DEPARTMENT_BROWSER:
                return getDepartmentName(fieldValue, recordSet);
            case MULTI_DEPARTMENT_BROWSER:
                return Arrays.stream(fieldValue.split(",")).map(i -> getDepartmentName(i, recordSet))
                        .collect(Collectors.joining(","));
            case CUSTOM_MODE_BROWSER:
                if (fieldValue.contains(",")) {
                    return Arrays.stream(fieldValue.split(",")).map(i ->
                                    getModeBrowserFieldShowName(i,fieldId, recordSet))
                            .collect(Collectors.joining(","));
                }
                return getModeBrowserFieldShowName(fieldValue,fieldId, recordSet);
            case SINGLE_DOC_BROWSER:
                return getDocTitle(fieldValue, recordSet);
            case MULTI_DOC_BROWSER:
                return Arrays.stream(fieldValue.split(",")).map(i -> getDocTitle(i, recordSet))
                        .collect(Collectors.joining(","));
            default:
                log.info("不支持的字段类型："+fieldType);
                return fieldValue;
        }
    }

    private String getDepartmentName(String departmentId, RecordSet recordSet) {
        recordSet.executeQuery("select DEPARTMENTNAME from hrmdepartment where id =?", departmentId);
        recordSet.next();
        return recordSet.getString("DEPARTMENTNAME");
    }

    private String getModeBrowserFieldShowName(String value,int fieldId, RecordSet recordSet) {
        String fieldDbType = getFieldDbType(fieldId, recordSet);
        if (StrUtil.isEmpty(fieldDbType)) {
            log.error("fielddbtype为空，字段ID："+fieldId);
            return value;
        }
        if (!fieldDbType.contains("browser")) {
            log.error("字段类型不为browser");
            return value;
        }
        String browserName = fieldDbType.split("\\.")[1];
        int browserId = getModeBrowserId(recordSet, browserName);
        String tableName = getModeBrowserTableName(recordSet, browserId);
        String showFieldName = getModeBrowserShowField(recordSet, browserId);
        if (StrUtil.isEmpty(showFieldName)) {
            log.error("找不到浏览框链接字段，字段ID："+fieldId);
            return value;
        }
        log.info("表名："+tableName+"，字段名："+showFieldName);
        recordSet.executeQuery("select " + showFieldName + " from " + tableName + " where id=?", value);
        recordSet.next();
        return recordSet.getString(showFieldName);
    }

    private static String getModeBrowserTableName(RecordSet recordSet, int browserId) {
        recordSet.executeQuery("SELECT b.tablename from mode_custombrowser m " +
                "join workflow_bill b on m.formid =b.id WHERE m.id=?", browserId);
        recordSet.next();
        return recordSet.getString("tablename");
    }

    private static int getModeBrowserId(RecordSet recordSet, String browserName) {
        recordSet.executeQuery("SELECT CUSTOMID from mode_browser b where  b.SHOWNAME = ?", browserName);
        recordSet.next();
        return recordSet.getInt("CUSTOMID");
    }

    private static String getFieldDbType(int fieldId, RecordSet recordSet) {
        recordSet.executeQuery("select fielddbtype from workflow_billfield where id=?", fieldId);
        recordSet.next();
        return recordSet.getString("fielddbtype");
    }

    private String getModeBrowserShowField(RecordSet recordSet,int browserId) {
        recordSet.executeQuery("SELECT fieldid from mode_custombrowserdspfield WHERE customid=? and ISTITLE='1'",
                browserId);
        recordSet.next();
        String showFieldId = recordSet.getString("fieldid");
        if (StrUtil.isEmpty(showFieldId)) {
            return "";
        }
        recordSet.executeQuery("select fieldname from workflow_billfield where id=?", showFieldId);
        recordSet.next();
        return recordSet.getString("fieldname");
    }

    private static String  getDocTitle(String fieldValue, RecordSet recordSet) {
        recordSet.executeQuery("select docsubject from docdetail where id=?", fieldValue);
        recordSet.next();
        return recordSet.getString("docsubject");
    }
}
