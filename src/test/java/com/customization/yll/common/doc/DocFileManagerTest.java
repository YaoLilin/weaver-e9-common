package com.customization.yll.common.doc;

import com.customization.yll.common.doc.bean.DocFileInfo;
import com.customization.yll.common.util.FileUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author 姚礼林
 * @desc 获取文档文件测试
 * @date 2025/6/27
 **/
public class DocFileManagerTest {

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void getDocFiles() throws IOException {
        String savePath = GCONST.getRootPath() + "filesystem" + FileUtil.getSeparator() + "temp" + FileUtil.getSeparator()
                + "files-" + System.currentTimeMillis();
        DocFileManager manager = new DocFileManager();
        List<DocFileInfo> docFiles = manager.getDocFiles(345, savePath);
        System.out.println("docFiles:"+docFiles);
        Assert.assertFalse(docFiles.isEmpty());
    }

}
