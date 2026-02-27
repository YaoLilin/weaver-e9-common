package com.customization.yll.common.workflow;

import cn.hutool.core.collection.CollUtil;
import com.customization.yll.common.util.TestUtil;
import com.customization.yll.common.workflow.constants.WorkflowLogType;
import com.customization.yll.common.workflow.entity.WorkflowApprovalInfoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 流程审批意见日志获取测试
 * @date 2025/11/5
 **/
class WorkflowApprovalInfoManagerTest {

    @BeforeEach
    void setUp() {
        TestUtil.setLocalServer();
    }

    @Test
    void getWorkflowApprovalInfo() {
        WorkflowApprovalInfoManager manager = new WorkflowApprovalInfoManager();
        List<WorkflowApprovalInfoEntity> logList = manager
                .getWorkflowApprovalInfo(CollUtil.toList(WorkflowLogType.APPROVE,
                                WorkflowLogType.SUBMIT), true, 2508525,
                        CollUtil.toList(26526));
    }

}
