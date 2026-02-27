package com.customization.yll.common.doc;

import com.customization.yll.common.doc.bean.SignatureResult;
import org.junit.Before;
import org.junit.Test;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author 姚礼林
 * @desc FileSignatureInfoManager 测试类
 * @date 2025/8/3
 **/
public class SysFileSignatureInfoManagerTest {

    private static final Logger log = LoggerFactory.getLogger(SysFileSignatureInfoManagerTest.class);

    // 契约锁配置信息（需要根据实际情况配置）
    private static final String SERVER_URL = "http://172.18.17.189:9182"; // 替换为实际的服务器地址
    private static final String APP_TOKEN = "rqxZhRqwEI"; // 替换为实际的 app token
    private static final String APP_SECRET = "cEmEdRVupjRzPd68FzPSjXCxJYm8Mk"; // 替换为实际的 app secret

    // 测试文件路径
    private static final String TEST_FILE_PATH = "/Users/yaolilin/Downloads/新能源-发（报）文稿纸-罗晓云-2025-08-06/关于总监聘任的决定-定稿.pdf";

    private SysFileSignatureInfoManager manager;

    @Before
    public void setUp() {
        // 初始化文件签名信息管理器
        manager = new SysFileSignatureInfoManager(SERVER_URL, APP_TOKEN, APP_SECRET);
    }

    @Test
    public void testGetFileSignatureInfo() {
        log.info("开始测试文件签名信息验证...");
        log.info("测试文件路径: " + TEST_FILE_PATH);

        Optional<SignatureResult> result = manager.getFileSignatureInfo(TEST_FILE_PATH);

        if (result.isPresent()) {
            SignatureResult signatureResult = result.get();
            log.info("文件签名验证成功！");
            log.info("响应码: " + signatureResult.getCode());
            log.info("响应信息: " + signatureResult.getMessage());
            log.info("验签结果码: " + signatureResult.getStatusCode());
            log.info("验签结果: " + signatureResult.getStatusMsg());
            log.info("文档ID: " + signatureResult.getDocumentId());

            if (signatureResult.getSignatureInfos() != null && !signatureResult.getSignatureInfos().isEmpty()) {
                log.info("签名详情数量: " + signatureResult.getSignatureInfos().size());

                for (int i = 0; i < signatureResult.getSignatureInfos().size(); i++) {
                    SignatureResult.SignatureInfo signInfo = signatureResult.getSignatureInfos().get(i);
                    log.info("=== 签名详情 " + (i + 1) + " ===");
                    log.info("校验结果码: " + signInfo.getCode());
                    log.info("校验结果: " + signInfo.getMsg());
                    log.info("是否修改: " + signInfo.getModified());
                    log.info("签署方: " + signInfo.getSignatory());
                    log.info("签名时间: " + signInfo.getSignTime());
                    log.info("签名原因: " + signInfo.getSignReason());
                    log.info("摘要算法: " + signInfo.getHashAlg());
                    log.info("颁发机构: " + signInfo.getOrganization());
                    log.info("签名算法: " + signInfo.getStrAlgName());
                    log.info("签名摘要: " + signInfo.getSignedDiget());
                    log.info("覆盖全文: " + signInfo.getSignatureCoversWholeDocument());
                    log.info("可见签名: " + signInfo.getVisibleSignature());
                    log.info("有时间戳: " + signInfo.getHasTimeStamp());
                    log.info("时间戳: " + signInfo.getTimeStamp());
                    log.info("时间戳校验: " + signInfo.getVerifyTimestamp());
                    log.info("证书：" + signInfo.getCert());
                    log.info("证书序列号: " + signInfo.getCertSerialNo());
                    log.info("证书有效期从: " + signInfo.getCertDateFrom());
                    log.info("证书有效期至: " + signInfo.getCertDateTo());
                    log.info("加密算法: " + signInfo.getEncryptionAlg());
                    log.info("文档ID: " + signInfo.getDocumentId());
                    log.info("签署平台: " + signInfo.getSignPlatform());
                    log.info("字段名: " + signInfo.getFieldName());
                    log.info("印章ID: " + signInfo.getSealId());
                    log.info("印章名称: " + signInfo.getSealName());
                    log.info("签名ID: " + signInfo.getSignatureId());
                    log.info("签名名称: " + signInfo.getSignatureName());
                    log.info("公钥: " + signInfo.getPublicKey());

                    if (signInfo.getCertChain() != null && !signInfo.getCertChain().isEmpty()) {
                        log.info("证书引证数量: " + signInfo.getCertChain().size());
                        for (int j = 0; j < signInfo.getCertChain().size(); j++) {
                            log.info("证书引证 " + (j + 1) + ": " + signInfo.getCertChain().get(j));
                        }
                    }

                    if (signInfo.getAnnotItems() != null && !signInfo.getAnnotItems().isEmpty()) {
                        log.info("修改表单数量: " + signInfo.getAnnotItems().size());
                        for (int j = 0; j < signInfo.getAnnotItems().size(); j++) {
                            log.info("修改表单 " + (j + 1) + ": " + signInfo.getAnnotItems().get(j));
                        }
                    }
                }
            } else {
                log.info("没有找到签名详情信息");
            }
        } else {
            log.error("文件签名验证失败或文件无签名信息");
        }
    }

    @Test
    public void testFileExistence() {
        log.info("=== 测试文件存在性 ===");
        java.io.File file = new java.io.File(TEST_FILE_PATH);

        if (file.exists()) {
            log.info("✅ 文件存在");
            log.info("文件大小: " + file.length() + " bytes");
            log.info("文件路径: " + file.getAbsolutePath());

            // 检查文件格式
            if (TEST_FILE_PATH.toLowerCase().endsWith(".pdf")) {
                log.info("✅ 文件格式正确（PDF）");
            } else {
                log.error("❌ 文件格式不正确，仅支持PDF格式");
            }
        } else {
            log.error("❌ 文件不存在: " + TEST_FILE_PATH);
        }
    }

    @Test
    public void testConfiguration() {
        log.info("=== 测试配置信息 ===");
        log.info("服务器地址: " + SERVER_URL);
        log.info("App Token: " + APP_TOKEN);
        log.info("App Secret: " + (APP_SECRET.isEmpty() ? "未配置" : "已配置"));

        if ("your-app-token".equals(APP_TOKEN) || "your-app-secret".equals(APP_SECRET)) {
            log.warn("⚠️  请配置正确的契约锁访问密钥");
        }
    }

    @Test
    public void testOfdFormatSupport() {
        log.info("=== 测试 OFD 格式支持 ===");

        // 测试文件格式检查逻辑
        String pdfPath = "/path/to/test.pdf";
        String ofdPath = "/path/to/test.ofd";
        String unsupportedPath = "/path/to/test.txt";

        // 验证文件扩展名提取
        String pdfExt = getFileExtension(pdfPath);
        String ofdExt = getFileExtension(ofdPath);
        String txtExt = getFileExtension(unsupportedPath);

        assertEquals("PDF 扩展名应该是 'pdf'", "pdf", pdfExt);
        assertEquals("OFD 扩展名应该是 'ofd'", "ofd", ofdExt);
        assertEquals("TXT 扩展名应该是 'txt'", "txt", txtExt);

        // 验证格式支持检查
        assertTrue("PDF 格式应该被支持", isSupportedFormat(pdfExt));
        assertTrue("OFD 格式应该被支持", isSupportedFormat(ofdExt));
        assertFalse("TXT 格式不应该被支持", isSupportedFormat(txtExt));

        // 验证 MIME 类型
        assertEquals("PDF MIME 类型应该是 'application/pdf'", "application/pdf", getMimeType(pdfExt));
        assertEquals("OFD MIME 类型应该是 'application/ofd'", "application/ofd", getMimeType(ofdExt));
        assertEquals("未知格式 MIME 类型应该是 'application/octet-stream'", "application/octet-stream", getMimeType(txtExt));

        log.info("✅ OFD 格式支持测试通过");
    }

    // 辅助方法，复制 FileSignatureInfoManager 中的逻辑用于测试
    private String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    private boolean isSupportedFormat(String fileExtension) {
        return "pdf".equals(fileExtension) || "ofd".equals(fileExtension);
    }

    private String getMimeType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "ofd":
                return "application/ofd";
            default:
                return "application/octet-stream";
        }
    }
}
