package com.customization.yll.common.doc;

import cn.hutool.core.io.FileUtil;
import com.customization.yll.common.doc.bean.DocQRConfig;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.io.IOException;

/**
 * @author 姚礼林
 * @desc 文档插入二维码测试
 * @date 2025/3/28
 **/
public class DocInsertQrManagerTest {

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void insertQr_withDocx() {
        DocInsertQrManager docInsertQrManager = new DocInsertQrManager(new DocQRConfig());
        String outDocPath = "/Users/yaolilin/Downloads/out_qr.docx";
        boolean result = docInsertQrManager.insertQr("hello",
                "/Users/yaolilin/Downloads/（城职院）2025年国家统一法律职业资格考试（8.15）-确认版.docx", outDocPath);
        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.exist(outDocPath));
    }

    @Test
    public void insertQr_withDoc() {
        DocInsertQrManager docInsertQrManager = new DocInsertQrManager(new DocQRConfig());
        String outDocPath = "/Users/yaolilin/Desktop/test_qr.doc";
        boolean result = docInsertQrManager.insertQr("hello",
                "/Users/yaolilin/Desktop/test.doc", outDocPath);
        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.exist(outDocPath));
    }

    @Test
    public void insertQr_withSameFile() {
        DocInsertQrManager docInsertQrManager = new DocInsertQrManager(new DocQRConfig());
        String filePath = "/Users/yaolilin/Desktop/test.docx";
        boolean result = docInsertQrManager.insertQr("hello", filePath, filePath);
        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.exist(filePath));
    }

    @Test
    public void insertQr_withConfig() {
        DocQRConfig docQRConfig = new DocQRConfig();
        docQRConfig.setQrAlign(ParagraphAlignment.CENTER);
        docQRConfig.setQrMarginTop(50);
        docQRConfig.setQrMarginBottom(100);
        docQRConfig.setQrSize(100);
        docQRConfig.setQrImageSize(100);
        DocInsertQrManager docInsertQrManager = new DocInsertQrManager(docQRConfig);
        String outDocPath = "/Users/yaolilin/Desktop/test_qr.docx";
        boolean result = docInsertQrManager.insertQr("hello",
                "/Users/yaolilin/Desktop/test.docx", outDocPath);
        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.exist(outDocPath));
    }

    @Test
    public void insertQr_containQr_removeAndInsert() throws IOException {
        DocInsertQrManager docInsertQrManager = new DocInsertQrManager(new DocQRConfig());
        String docFilePath = "/Users/yaolilin/Desktop/test_contain_qr.docx";
        String outDocPath = "/Users/yaolilin/Desktop/test_qr.docx";
        boolean result = docInsertQrManager.insertQr("hello 3", docFilePath, outDocPath);
        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.exist(outDocPath));
        String qrCodeContent = docInsertQrManager.getQrCodeContent(outDocPath);
        Assert.assertEquals("hello 3", qrCodeContent);
    }

    @Test
    public void containsQRCode() throws IOException {
        DocInsertQrManager docInsertQrManager = new DocInsertQrManager(new DocQRConfig());
        String docFilePath = "/Users/yaolilin/Desktop/test_contain_qr.docx";
        boolean containsQRCode = docInsertQrManager.containsQRCode(docFilePath);
        Assert.assertTrue(containsQRCode);
    }

    @Test
    public void getQrCodeContent() throws IOException {
        DocInsertQrManager docInsertQrManager = new DocInsertQrManager(new DocQRConfig());
        String docFilePath = "/Users/yaolilin/Desktop/test.docx";
        String outDocPath = "/Users/yaolilin/Desktop/test_qr.docx";
        docInsertQrManager.insertQr("hello", docFilePath, outDocPath);
        String qrCodeContent = docInsertQrManager.getQrCodeContent(outDocPath);
        Assert.assertEquals("hello", qrCodeContent);
    }
}
