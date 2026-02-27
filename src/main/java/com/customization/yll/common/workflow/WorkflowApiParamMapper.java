package com.customization.yll.common.workflow;

import com.customization.yll.common.manager.WorkflowFieldMapper;

/**
 * @author yaolilin
 * @desc todo
 * @date 2024/12/12
 **/
public class WorkflowApiParamMapper {
    private final WorkflowFieldMapper fieldMapper = new WorkflowFieldMapper();
    private final int configId;

    public WorkflowApiParamMapper(int configId) {
        this.configId = configId;
    }

    public void addParam(String paramName) {

    }
}
