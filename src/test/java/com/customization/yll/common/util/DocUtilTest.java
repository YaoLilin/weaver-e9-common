package com.customization.yll.common.util;

import com.customization.yll.common.doc.bean.DocFileInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.conn.RecordSet;
import weaver.general.GCONST;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 文档工具类测试
 * @date 2025/6/27
 **/
public class DocUtilTest {

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void getDocFileInfos() {
        List<DocFileInfo> docFileInfos = DocUtil.getDocFileInfos(355,new RecordSet());
        System.out.println("docFileInfos:"+docFileInfos);
        Assert.assertFalse(docFileInfos.isEmpty());
    }

    @Test
    public void createDocByFile() {
        String filePath = "/Users/yaolilin/Downloads/银河系关税减免申请流程-2025-08-29.zip";
        File file = new File(filePath);
        Optional<Integer> docId = DocUtil.createDocByFile(filePath, 1,
                file.getName(), 1);
        Assert.assertTrue(docId.isPresent());
        System.out.println("docId:" + docId.get());

    }
}
