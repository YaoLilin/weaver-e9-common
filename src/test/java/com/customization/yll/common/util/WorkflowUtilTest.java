package com.customization.yll.common.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.conn.RecordSet;
import weaver.general.GCONST;

import java.util.List;

/**
 * @author yaolilin
 * @desc WorkflowUtil 测试
 * @date 2024/12/19
 **/
public class WorkflowUtilTest {

    @Before
    public void init() {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void isCurrentOrAfterWorkflowVersion_withTrue() {
        boolean result = WorkflowUtil.isCurrentOrAfterWorkflowVersion(44, 55);
        Assert.assertTrue(result);
    }

    @Test
    public void isCurrentOrAfterWorkflowVersion_withSameVersion_willTrue() {
        boolean result = WorkflowUtil.isCurrentOrAfterWorkflowVersion(55, 55);
        Assert.assertTrue(result);
    }

    @Test
    public void isCurrentOrAfterWorkflowVersion_withFalse() {
        boolean result = WorkflowUtil.isCurrentOrAfterWorkflowVersion(47, 55);
        Assert.assertFalse(result);
    }

    @Test
    public void getLatterVersionsWorkflowIds() {
        List<Integer> result = WorkflowUtil.getLatterVersionsWorkflowIds(44);
        Assert.assertFalse(result.isEmpty());
        System.out.println(result);
    }

    @Test
    public void getCurrentNodeOperatorIds() {
        List<Integer> operatorIds = WorkflowUtil.getCurrentNodeOperatorIds(207214,
                null, new RecordSet());
        System.out.println(operatorIds);
        Assert.assertFalse(operatorIds.isEmpty());
        operatorIds = WorkflowUtil.getCurrentNodeOperatorIds(207214,
                0, new RecordSet());
        System.out.println(operatorIds);
        Assert.assertFalse(operatorIds.isEmpty());
    }

    @Test
    public void getSelectItemShowName() {
        RecordSet recordSet = new RecordSet();
        String showName = WorkflowUtil.getSelectItemShowName("formtable_main_16", "xlk",
                1, recordSet);
        Assert.assertEquals("选项2", showName);
    }
}
