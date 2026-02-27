package com.customization.yll.common.workflow;

import com.customization.yll.common.IntegrationLog;
import com.customization.yll.common.util.WorkflowUtil;
import org.jetbrains.annotations.NotNull;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc WorkflowAction 帮助类，提供一些实用方法，比如获取字段值，更改流程表单字段值
 * @date 2025/9/15
 **/
public class WorkflowActionHelper {
    private final IntegrationLog log = new IntegrationLog(WorkflowActionHelper.class);
    private final RequestInfo requestInfo;
    private Map<String, String> mainFieldValue = null;

    public WorkflowActionHelper(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    @NotNull
    public String getMainFieldValue(String fieldName) {
        if (mainFieldValue == null) {
            initMainFieldValueMap();
        }
        String value = mainFieldValue.get(fieldName);
        if (value == null) {
            log.error("流程中没有此字段, {}", fieldName);
            return "";
        }
        return value;
    }

    /**
     * 获取明细字段值集合
     * @param detailIndex 明细索引，第一个明细的索引是 0
     * @param fieldName 字段名
     * @return 明细字段值, list 每个元素标识一行明细数据，map 中的 key 为字段名，value 为字段值
     */
    @NotNull
    public List<Map<String, String>> getDetailFieldValue(int detailIndex, String ...fieldName) {
        return WorkflowUtil.getDetailData(this.requestInfo.getDetailTableInfo().getDetailTable(detailIndex)
                , fieldName);
    }

    /**
     * 修改主表字段
     * @param fieldName 字段名
     * @param value 值
     * @return 修改成功返回 true，修改失败返回 false
     */
    public boolean changeMainFieldValue(String fieldName, String value) {
        Map<String, Object> data = new HashMap<>(1);
        data.put(fieldName, value);
        return WorkflowUtil.updateMainFieldValue(data, this.requestInfo);
    }

    /**
     * 修改主表字段
     * @param fieldData 字段数据, key 为字段名，value 为字段值
     * @return 修改成功返回 true，修改失败返回 false
     */
    public boolean changeMainFieldValue(Map<String ,Object> fieldData) {
        return WorkflowUtil.updateMainFieldValue(fieldData, this.requestInfo);
    }

    private void initMainFieldValueMap() {
        this.mainFieldValue = new HashMap<>(10);
        for (Property field : this.requestInfo.getMainTableInfo().getProperty()) {
            mainFieldValue.put(field.getName(), field.getValue());
        }
    }

}
