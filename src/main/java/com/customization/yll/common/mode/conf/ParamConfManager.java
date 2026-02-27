package com.customization.yll.common.mode.conf;

import com.customization.yll.common.exception.FieldValueEmptyException;
import com.customization.yll.common.mode.conf.entity.ParamConfigurationEntity;
import com.customization.yll.common.workflow.WorkflowFieldValueManager;
import org.apache.commons.lang.StringUtils;

/**
 * @author yaolilin
 * @desc 用于获取建模中配置的参数的值，仅适用于配置字段参数的配置建模
 * @date 2024/9/11
 **/
public class ParamConfManager {

    private final WorkflowFieldValueManager fieldValueManager ;

    public ParamConfManager(WorkflowFieldValueManager fieldValueManager) {
        this.fieldValueManager = fieldValueManager;
    }

    public String getParamValue(ParamConfigurationEntity entity){
        verifyConf(entity);
        String value = getValue(entity);
        if (entity.isRequired() && StringUtils.isEmpty(value)) {
            throw new FieldValueEmptyException(String.format("[%s] 字段值不能为空",entity.getName()));
        }
        return value;
    }

    private void verifyConf(ParamConfigurationEntity entity) {
        if (entity.isRequired()) {
            String name = entity.getName();
            if (StringUtils.isEmpty(name)) {
                throw new FieldValueEmptyException("字段名称不能为空");
            }
            boolean isConfValue = !StringUtils.isEmpty(entity.getDefaultValue()) || !StringUtils.isEmpty(entity.getFixedValue())
                    || entity.getWorkflowFieldId() != null || entity.getSysParam() != null;
            if (!isConfValue) {
                throw new FieldValueEmptyException(String.format("[%s] 必需配置默认值、固定值、系统参数或流程字段",name));
            }
        }
    }

    private String getValue(ParamConfigurationEntity entity) {
        if (!StringUtils.isEmpty(entity.getFixedValue())) {
            return entity.getFixedValue();
        }
        String value = null;
        if (entity.getWorkflowFieldId() != null) {
            value = fieldValueManager.getFieldValue(entity.getWorkflowFieldId(),entity.getGetWorkflowFieldDataWay());
        } else if (entity.getSysParam() != null) {
            value = fieldValueManager.getSystemFieldValue(entity.getSysParam());
        }
        return StringUtils.isEmpty(value) ? entity.getDefaultValue() : value;
    }
}
