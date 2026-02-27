package com.customization.yll.common.workflow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.enu.LanguageType;
import com.customization.yll.common.exception.SqlExecuteException;
import com.customization.yll.common.util.*;
import com.customization.yll.common.workflow.constants.GetWorkflowFieldDataWay;
import com.customization.yll.common.workflow.constants.SystemParam;
import com.customization.yll.common.workflow.interfaces.WorkflowFieldValueFetchInterface;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.util.*;

/**
 * @author yaolilin
 * @desc 获取流程字段值
 * @date 2024/8/26
 **/
public class WorkflowFieldValueManager implements WorkflowFieldValueFetchInterface {
    private Integer requestId;
    private String workflowTableName;
    private Integer workflowId;
    private final RecordSet recordSet = new RecordSet();
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @param requestId 流程请求id
     * @deprecated 该构造方法已经过时，请使用无参构造
     */
    @Deprecated
    public WorkflowFieldValueManager(int requestId) {
        this.requestId = requestId;
    }

    public WorkflowFieldValueManager() {
    }

    /**
     * 根据字段id获取流程字段值
     *
     * @param fieldId 字段id，不需要带上"filed"
     * @return 字段值
     * @deprecated 该方法已过时，请使用 {@link #getFieldValueByFieldId(int, int)}
     */
    @Deprecated
    public String getFieldValue(int fieldId) {
        return getFieldValue(fieldId, null);
    }

    /**
     * 获取流程字段值，如果没有指定 {@link GetWorkflowFieldDataWay} 则直接获取表单字段值，不进行转换
     *
     * @param fieldId                 字段id，不需要带上"filed"
     * @param getWorkflowFieldDataWay 字段取值方式，比如如果字段是下拉框，可以取下拉框的显示名，如果是人力资源字段，可以获取人力
     *                                资源字段的显示名
     * @return 字段值
     * @deprecated 该方法已过时，请使用 {@link #getFieldValueByFieldId(int, int, GetWorkflowFieldDataWay)}
     */
    @Deprecated
    public String getFieldValue(int fieldId, GetWorkflowFieldDataWay getWorkflowFieldDataWay) {
        if (this.requestId == null) {
            throw new IllegalArgumentException("请求id不能为空");
        }
        return this.getFieldValueByFieldId(this.requestId, fieldId, getWorkflowFieldDataWay);
    }

    /**
     * 获取流程字段值，如果没有指定 {@link GetWorkflowFieldDataWay} 则直接获取表单字段值，不进行转换
     *
     * @param requestId               流程请求id
     * @param fieldId                 字段id，不需要带上"filed"
     * @param getWorkflowFieldDataWay 字段取值方式，比如如果字段是下拉框，可以取下拉框的显示名，如果是人力资源字段，可以获取人力
     *                                资源字段的显示名
     * @return 字段值
     */
    @Override
    public String getFieldValueByFieldId(int requestId, int fieldId, GetWorkflowFieldDataWay getWorkflowFieldDataWay) {
        setRequestIdAndInit(requestId);
        String fieldName = FormUtil.getFieldName(fieldId, workflowId, recordSet);
        String value = getFieldValueByFieldName(fieldName, this.workflowTableName);
        if (StringUtils.isEmpty(value) || getWorkflowFieldDataWay == null) {
            return value;
        }
        return getFieldValueByWay(value, fieldId, getWorkflowFieldDataWay);
    }

    private String getFieldValueByFieldName(String fieldName, String workflowTableName) {
        String sql = "select " + fieldName + " from " + workflowTableName +
                " where requestid=?";
        if (!recordSet.executeQuery(sql, requestId)) {
            log.error("sql 查询错误，sql:" + sql);
            return "";
        }
        if (!recordSet.next()) {
            log.warn("无此请求id流程数据，请求id：" + this.requestId);
            return "";
        }
        return recordSet.getString(fieldName);
    }

    private void setRequestIdAndInit(int requestId) {
        if (this.requestId == null || this.requestId != requestId
                || this.workflowTableName == null || this.workflowId == null) {
            this.requestId = requestId;
            this.workflowTableName = null;
            this.workflowId = null;
            init();
        }
    }

    private void init() {
        recordSet.executeQuery("SELECT workflowid from workflow_requestbase WHERE requestid=?", requestId);
        if (!recordSet.next()) {
            log.error("无法根据此请求id查询流程id，请求id：" + requestId);
            return;
        }
        this.workflowId = recordSet.getInt("workflowid");
        this.workflowTableName = WorkflowUtil.getWorkflowTableName(workflowId, recordSet);
    }

    private String getFieldValueByWay(String value, int fieldId, GetWorkflowFieldDataWay getWorkflowFieldDataWay) {
        switch (getWorkflowFieldDataWay) {
            case SELECTOR_TEXT:
                return getSelectorText(value, fieldId);
            case HRM_TEXT:
                return getHrmText(value);
            case DEPARTMENT_TEXT:
                return getDepartmentText(value);
            case SUB_COMPANY_TEXT:
                return getSubCompanyName(value);
            default:
                throw new IllegalArgumentException("未找到对应的字段取值方式，取值方式：" + getWorkflowFieldDataWay);
        }
    }

    private String getSelectorText(String value, int fieldId) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (!value.contains(",")) {
            return WorkflowUtil.getSelectItemShowName(fieldId, Integer.parseInt(value), recordSet);
        }
        List<String> names = new ArrayList<>();
        for (String v : value.split(",")) {
            names.add(WorkflowUtil.getSelectItemShowName(fieldId, Integer.parseInt(v), recordSet));
        }
        return StringUtils.join(names, ",");
    }

    /**
     * 根据字段id获取流程字段值
     *
     * @param requestId 流程请求id
     * @param fieldId   字段id，不需要带上"filed"
     * @return 字段值
     */
    @Override
    public String getFieldValueByFieldId(int requestId, int fieldId) {
        return this.getFieldValueByFieldId(requestId, fieldId, null);
    }

    /**
     * 根据字段名获取流程字段值
     *
     * @param fieldName 字段数据库名
     * @param requestId 请求id
     * @return 字段值，如果获取不到则返回空字符串
     */
    @Override
    public String getFieldValueByFieldName(int requestId, String fieldName) {
        if (StrUtil.isBlank(fieldName)) {
            return "";
        }
        setRequestIdAndInit(requestId);
        return getFieldValueByFieldName(fieldName, this.workflowTableName);
    }

    /**
     * 获取多个流程字段值，如果获取不到则返回空 map
     *
     * @param requestId  流程请求id
     * @param fieldNames 需要获取字段值的字段名集合
     * @return 字段名对应的字段值 map，如果获取不到则返回空 map
     */
    @Override
    public Map<String, String> getFieldValueByFieldNames(int requestId, List<String> fieldNames) {
        if (CollUtil.isEmpty(fieldNames)) {
            return Collections.emptyMap();
        }
        setRequestIdAndInit(requestId);
        String sql = SqlUtil.buildQuerySql(fieldNames, workflowTableName);
        sql += " where requestid=?";
        recordSet.executeQuery(sql, requestId);
        if (!recordSet.next()) {
            log.warn("无此请求id流程数据，请求id：" + this.requestId);
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>(fieldNames.size());
        for (String fieldName : fieldNames) {
            result.put(fieldName, recordSet.getString(fieldName));
        }
        return result;
    }

    /**
     * 获取流程指定明细的多个字段的值
     *
     * @param requestId  流程请求id
     * @param detailNum  流程明细表序号，必需大于0，例如明细表1的序号为1
     * @param fieldNames 需要获取字段值的字段名集合
     * @return 明细字段的值
     * @throws IllegalArgumentException 参数不正确
     * @throws SqlExecuteException      如果查询流程明细表失败则抛出异常
     */
    @Override
    public List<Map<String, String>> getDetailFields(int requestId, int detailNum, List<String> fieldNames)
            throws IllegalArgumentException, SqlExecuteException {
        if (requestId < 1) {
            throw new IllegalArgumentException("请传入正确的请求id");
        }
        if (detailNum < 1) {
            throw new IllegalArgumentException("请传入正确的明细表序号，必需大于 0");
        }
        if (CollUtil.isEmpty(fieldNames)) {
            throw new IllegalArgumentException("明细表字段名集合不能为空");
        }
        String workflowTableName = WorkflowUtil.getWorkflowTableNameByRequestId(requestId, recordSet);
        if (StrUtil.isEmpty(workflowTableName)) {
            throw new IllegalArgumentException("无法获取流程表名，请检查请求id是否正确");
        }
        String detailTableName = workflowTableName + "_dt" + detailNum;
        String querySql = SqlUtil.buildQuerySql(fieldNames, detailTableName, "t");
        querySql += " JOIN " + workflowTableName + " z ON z.id = t.mainid AND z.requestid=?";
        if (!recordSet.executeQuery(querySql, requestId)) {
            throw new SqlExecuteException("查询流程明细表失败，sql:" + querySql);
        }

        List<Map<String, String>> detailData = new ArrayList<>();
        while (recordSet.next()) {
            Map<String, String> row = new HashMap<>(20);
            for (String fieldName : fieldNames) {
                row.put(fieldName, recordSet.getString(fieldName));
            }
            detailData.add(row);
        }
        log.info("查询到的明细数据记录数量：" + detailData.size());

        return detailData;
    }

    /**
     * 获取流程的系统字段值，比如标题，紧急程度等
     *
     * @param systemParam 系统字段
     * @return 系统字段值
     */
    @Override
    public String getSystemFieldValue(SystemParam systemParam) {
        switch (systemParam) {
            case TITLE:
                return getTitle();
            case URGENT_LEVEL:
                return getUrgentLevel();
            case CREAT_DATE:
                return getCreateDate();
            case CREATE_DATE_TIME:
                return getCreateDateTime();
            case ARCHIVE_DATE:
                return getArchiveDate();
            case ARCHIVE_DATE_TIME:
                return getArchiveDateTime();
            case CREATOR:
                return getCreatorName();
            case CREATE_DEPARTMENT:
                return getCreatorDepartment();
            default:
                throw new IllegalArgumentException("未定义系统字段获取方法，系统字段：" + systemParam);
        }
    }

    @NotNull
    private String getArchiveDateTime() {
        recordSet.executeQuery("select lastoperatedate,lastoperatetime from workflow_requestbase where requestid = ?", requestId);
        recordSet.next();
        return recordSet.getString("lastoperatedate") + " " + recordSet.getString("lastoperatetime");
    }

    private String getArchiveDate() {
        recordSet.executeQuery("select lastoperatedate from workflow_requestbase where requestid = ?", requestId);
        recordSet.next();
        return recordSet.getString("lastoperatedate");
    }

    @NotNull
    private String getCreateDateTime() {
        recordSet.executeQuery("select createdate,createtime from workflow_requestbase where requestid = ?", requestId);
        recordSet.next();
        return recordSet.getString("createdate") + " " + recordSet.getString("createtime");
    }

    private String getCreateDate() {
        recordSet.executeQuery("select createdate from workflow_requestbase where requestid = ?", requestId);
        recordSet.next();
        return recordSet.getString("createdate");
    }

    private String getTitle() {
        recordSet.executeQuery("select requestname from workflow_requestbase where requestid = ?", requestId);
        recordSet.next();
        return recordSet.getString("requestname");
    }

    private int getCreator() {
        recordSet.executeQuery("select creater from workflow_requestbase where requestid = ?", requestId);
        recordSet.next();
        return recordSet.getInt("creater");
    }

    private String getCreatorDepartment() {
        int creator = getCreator();
        recordSet.executeQuery("SELECT d.departmentname from hrmdepartment d join hrmresource h " +
                "on h.departmentid = d.id where h.id = ?", creator);
        recordSet.next();
        return recordSet.getString("departmentname");
    }

    private String getCreatorName() {
        int creator = getCreator();
        log.info("获取到创建人id：" + creator);
        String lastName = HrmInfoUtil.getLastName(creator, recordSet);
        log.info("创建人姓名：" + lastName);
        return MultiLanguageUtil.analyzeMultiLanguageText(lastName, LanguageType.CN, recordSet);
    }

    @NotNull
    private String getUrgentLevel() {
        recordSet.executeQuery("select requestlevel from workflow_requestbase where requestid = ?", requestId);
        recordSet.next();
        int requestLevel = recordSet.getInt("requestlevel");
        if (requestLevel == 0) {
            return "正常";
        } else if (requestLevel == 1) {
            return "重要";
        } else if (requestLevel == 2) {
            return "紧急";
        } else {
            return "";
        }
    }

    private String getHrmText(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (!value.contains(",")) {
            return HrmInfoUtil.getLastName(Integer.valueOf(value), recordSet);
        }
        List<String> names = new ArrayList<>();
        for (String v : value.split(",")) {
            names.add(HrmInfoUtil.getLastName(Integer.valueOf(v), recordSet));
        }
        return StringUtils.join(names, ",");
    }

    private String getDepartmentText(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (!value.contains(",")) {
            return getDepartmentName(value);
        }
        List<String> names = new ArrayList<>();
        for (String v : value.split(",")) {
            names.add(getDepartmentName(v));
        }
        return StringUtils.join(names, ",");
    }

    private String getSubCompanyName(String value) {
        if (StrUtil.isEmpty(value)) {
            return "";
        }
        if (value.contains(",")) {
            List<String> names = new ArrayList<>();
            for (String id : value.split(",")) {
                String subCompanyName = HrmInfoUtil.getSubCompanyName(Integer.parseInt(id), recordSet);
                names.add(subCompanyName);
            }
            return StringUtils.join(names, ",");
        }
        return HrmInfoUtil.getSubCompanyName(Integer.parseInt(value), recordSet);
    }

    private String getDepartmentName(String value) {
        recordSet.executeQuery("select departmentname from hrmdepartment where id=?", value);
        recordSet.next();
        return recordSet.getString("departmentname");
    }
}
