package com.customization.yll.common.bean;

import java.util.function.Function;

/**
 * @author 姚礼林
 * @desc 数据字段和接口参数字段映射配置对象
 * @date 2025/7/18
 **/
public class FieldParamMap extends MapInfo{
    private String valueFieldName;
    private String description;

    public FieldParamMap(String valueFieldName,String paramName) {
        super(paramName);
        this.valueFieldName = valueFieldName;
    }

    public FieldParamMap(String valueFieldName,String paramName,String description) {
        this(valueFieldName,paramName);
        this.description = description;
    }

    public FieldParamMap(String valueFieldName, String paramName, String description, boolean require) {
        this(valueFieldName,paramName,description);
        this.setNotEmpty(require);
    }

    public FieldParamMap(String valueFieldName, String paramName, String description, boolean require,
                         Function<String, Object> convertFunction) {
        this(valueFieldName,paramName,description,require);
        setConvertFunction(convertFunction);
    }

    public String getValueFieldName() {
        return valueFieldName;
    }

    public void setValueFieldName(String valueFieldName) {
        this.valueFieldName = valueFieldName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
