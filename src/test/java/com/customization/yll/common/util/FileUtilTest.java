package com.customization.yll.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * @author yaolilin
 * @desc 文件工具类测试
 * @date 2024/9/26
 **/
public class FileUtilTest {

    @Test
    public void getSuffix() {
        String fileName = "3434535nn.doc";
        assertEquals("doc",FileUtil.getSuffix(fileName));
    }

    @Test
    public void getFileMd5ToUpperCase() throws IOException, NoSuchAlgorithmException {
        String filePath = "/Users/yaolilin/Desktop/正文.docx";
        String md5 = FileUtil.getFileMd5ToUpperCase(filePath);
        System.out.println(md5);
        Assert.assertNotNull(md5);
    }

    @Test
    public void getFileMd5ToLowerCase() throws IOException, NoSuchAlgorithmException {
        String filePath = "/Users/yaolilin/Desktop/正文.docx";
        String md5 = FileUtil.getFileMd5ToLowerCase(filePath);
        System.out.println(md5);
        Assert.assertNotNull(md5);
    }
}
