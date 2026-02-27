package com.customization.yll.common.bean;

import com.customization.yll.common.enu.FieldType;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author 姚礼林
 * @desc 映射关系配置
 * @date 2024/7/10
 */
public class MapInfo{
    private String paramName;
    private boolean notEmpty;
    private Function<String ,Object> convertFunction;
    private Predicate<String> skipCondition;
    private FieldType fieldType;

    /**
     * @param paramName 映射到的名称
     */
    public MapInfo(String paramName) {
        this.paramName = paramName;
    }

    /**
     * @param paramName 映射到的名称
     * @param required 是否必填
     */
    public MapInfo(String paramName,boolean required) {
        this.paramName = paramName;
        notEmpty = required;
    }

    /**
     * @param paramName 映射到的名称
     * @param fileType 字段类型
     */
    public MapInfo(String paramName, FieldType fileType) {
        this.paramName = paramName;
        this.fieldType = fileType;
    }

    /**
     * @param paramName 映射到的名称
     * @param convertFunction 值转换函数，可对字段值进行转换
     */
    public MapInfo(String paramName,Function<String, Object> convertFunction) {
        this.paramName = paramName;
        this.convertFunction = convertFunction;
    }

    public String getParamName() {
        return paramName;
    }

    /**
     * @param paramName 映射到的名称
     */
    public MapInfo setParamName(String paramName) {
        this.paramName = paramName;
        return this;
    }

    public boolean isNotEmpty() {
        return notEmpty;
    }

    /**
     * 是否允许字段为空，如果为是，则会校验字段是否为空，如果字段出现空值则会抛出异常
     * @param notEmpty 是否为空
     */
    public MapInfo setNotEmpty(boolean notEmpty) {
        this.notEmpty = notEmpty;
        return this;
    }

    public Function<String, Object> getConvertFunction() {
        return convertFunction;
    }

    /**
     * 指定一个转换函数，可以将字段的值转为指定的值
     * @param convertFunction 转换函数
     */
    public MapInfo setConvertFunction(Function<String, Object> convertFunction) {
        this.convertFunction = convertFunction;
        return this;
    }

    public Predicate<String> getSkipCondition() {
        return skipCondition;
    }

    /**
     * 指定一个条件函数，如果字段的值满足条件则会跳过该字段，不进行映射
     * @param skipCondition 条件函数
     */
    public MapInfo setSkipCondition(Predicate<String> skipCondition) {
        this.skipCondition = skipCondition;
        return this;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    /**
     * 设置映射后的字段类型，映射字段值时就会转成指定的类型
     * @param fieldType 字段类型
     */
    public MapInfo setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
        return this;
    }
}
