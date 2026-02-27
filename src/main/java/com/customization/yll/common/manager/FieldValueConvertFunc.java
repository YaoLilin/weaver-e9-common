package com.customization.yll.common.manager;

import java.util.Map;

/**
 * @author 姚礼林
 * @desc 字段值转换函数
 * @date 2025/7/21
 **/
@FunctionalInterface
public interface FieldValueConvertFunc {

    /**
     * 转换字段值
     * @param value 原始字段值
     * @param otherFieldValue 其它字段值
     * @return 转换后的值
     */
    Object convert(String value, Map<String ,Object> otherFieldValue);
}
