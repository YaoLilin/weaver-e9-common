package com.customization.yll.common.workflow;

import cn.hutool.core.collection.CollUtil;
import com.customization.yll.common.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author 姚礼林
 * @desc 获取流程字段值测试
 * @date 2025/10/29
 **/
class WorkflowFieldValueManagerTest {
    private WorkflowFieldValueManager fieldValueManager;

    @BeforeEach
    void setUp() {
        TestUtil.setLocalServer();
    }

    @Test
    void getFieldValue() {
        int requestId = 615636;
        fieldValueManager = new WorkflowFieldValueManager(requestId);
        // 获取年度字段值
        String fieldValue = fieldValueManager.getFieldValue(6951);
        Assertions.assertFalse(fieldValue.isEmpty());
        System.out.println("字段值：" + fieldValue);
    }

    @Test
    void getFieldValueByFieldId() {
        fieldValueManager = new WorkflowFieldValueManager();
        String fieldValue = fieldValueManager.getFieldValueByFieldId(615636, 6951);
        Assertions.assertFalse(fieldValue.isEmpty());
        System.out.println("字段值：" + fieldValue);
    }

    @Test
    void getFieldValueByFieldName() {
        fieldValueManager = new WorkflowFieldValueManager();
        String fieldValue = fieldValueManager.getFieldValueByFieldName(615636, "nd");
        Assertions.assertFalse(fieldValue.isEmpty());
        System.out.println("字段值：" + fieldValue);
    }

    @Test
    void getFieldValueByFieldNames() {
        fieldValueManager = new WorkflowFieldValueManager();
        Map<String, String> valueMap = fieldValueManager
                .getFieldValueByFieldNames(615636, CollUtil.toList("nd", "cjrq"));
        Assertions.assertFalse(valueMap.isEmpty());
        System.out.println("字段值：" + valueMap);
    }
}
