package com.customization.yll.common.workflow.constants;

import com.customization.yll.common.exception.EnumNotPresentException;

/**
 * @author yaolilin
 * @desc 流程类型
 * @date 2024/8/23
 **/
public enum WorkflowType {
    /**
     * 发文
     */
    SEND_DOCUMENT(0),
    /**
     * 收文
     */
    RECEIVE_DOCUMENT(1),
    /**
     * 普通流程
     */
    NORMAL(2);

    private final int value;

    WorkflowType(int value) {
        this.value = value;
    }

    public static WorkflowType of(int value) {
        for (WorkflowType workflowType : values()) {
            if (workflowType.value == value) {
                return workflowType;
            }
        }
        throw new EnumNotPresentException("enum not found ,value:"+value);
    }
}
