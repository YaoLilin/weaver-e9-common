package com.customization.yll.common.workflow.constants;

import com.customization.yll.common.exception.EnumNotPresentException;

/**
 * @author yaolilin
 * @desc 流程字段类型
 * @date 2024/8/27
 **/
public enum GetWorkflowFieldDataWay {
    SELECTOR_TEXT(0), HRM_TEXT(1), DEPARTMENT_TEXT(2),
    SUB_COMPANY_TEXT(3);

    private final Integer value;

    GetWorkflowFieldDataWay(Integer value) {
        this.value = value;
    }

    public static GetWorkflowFieldDataWay of(Integer value) {
        for (GetWorkflowFieldDataWay type : GetWorkflowFieldDataWay.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new EnumNotPresentException("enum not found ,value:" + value);
    }
}
