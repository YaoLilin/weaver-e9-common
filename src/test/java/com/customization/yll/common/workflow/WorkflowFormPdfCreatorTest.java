package com.customization.yll.common.workflow;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.conn.RecordSet;
import weaver.general.GCONST;

import java.util.Optional;

/**
 * @author yaolilin
 * @desc 流程表单转pdf测试
 * @date 2025/2/27
 **/
public class WorkflowFormPdfCreatorTest {

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void createFormPdf() {
        WorkflowFormPdfCreator formPdfCreator = new WorkflowFormPdfCreator();
        Optional<Integer> formPdfId = formPdfCreator.createFormPdf(280, 375376);
        Assert.assertTrue(formPdfId.isPresent());
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select imagefilename from imagefile where imagefileid=?", formPdfId.get());
        recordSet.next();
        String fileName = recordSet.getString("imagefilename");
        Assert.assertFalse(fileName.isEmpty());
        System.out.println(fileName);
    }
}
