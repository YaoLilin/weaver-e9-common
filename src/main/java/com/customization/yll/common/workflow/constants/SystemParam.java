package com.customization.yll.common.workflow.constants;

import com.customization.yll.common.exception.EnumNotPresentException;

/**
 * @author yaolilin
 * @desc 系统字段
 * @date 2024/9/2
 **/
public enum SystemParam {
    TITLE(0), URGENT_LEVEL(1), CREAT_DATE(2), ARCHIVE_DATE(3), CREATE_DATE_TIME(4),
    ARCHIVE_DATE_TIME(5), CREATOR(6), CREATE_DEPARTMENT(7);
    private final int value;

    SystemParam(Integer value) {
        this.value = value;
    }

    public static SystemParam of(Integer value) {
        for (SystemParam systemParam : values()) {
            if (systemParam.value == value) {
                return systemParam;
            }
        }
        throw new EnumNotPresentException("enum not found ,value:" + value);
    }
}
