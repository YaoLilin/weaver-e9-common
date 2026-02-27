package com.customization.yll.common.mode.conf.entity;

import com.customization.yll.common.workflow.constants.GetWorkflowFieldDataWay;
import com.customization.yll.common.workflow.constants.SystemParam;

/**
 * @author yaolilin
 * @desc 接口参数映射配置信息
 * @date 2024/9/11
 **/
public class ParamConfigurationEntity {
    private String name;
    private String defaultValue;
    private String fixedValue;
    private SystemParam sysParam;
    private Integer workflowFieldId;
    private GetWorkflowFieldDataWay getWorkflowFieldDataWay;
    private boolean required;
    private boolean codeFixed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    public SystemParam getSysParam() {
        return sysParam;
    }

    public void setSysParam(SystemParam sysParam) {
        this.sysParam = sysParam;
    }

    public Integer getWorkflowFieldId() {
        return workflowFieldId;
    }

    public void setWorkflowFieldId(Integer workflowFieldId) {
        this.workflowFieldId = workflowFieldId;
    }

    public GetWorkflowFieldDataWay getGetWorkflowFieldDataWay() {
        return getWorkflowFieldDataWay;
    }

    public void setGetWorkflowFieldDataWay(GetWorkflowFieldDataWay getWorkflowFieldDataWay) {
        this.getWorkflowFieldDataWay = getWorkflowFieldDataWay;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isCodeFixed() {
        return codeFixed;
    }

    public void setCodeFixed(boolean codeFixed) {
        this.codeFixed = codeFixed;
    }
}
