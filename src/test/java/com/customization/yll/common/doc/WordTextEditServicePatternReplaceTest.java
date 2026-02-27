package com.customization.yll.common.doc;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * WordTextEditService 正则表达式替换功能测试类
 *
 * 测试新增的 replaceTextByPattern 方法
 *
 * @author 姚礼林
 * @date 2025
 */
class WordTextEditServicePatternReplaceTest {

    private static final Logger log = LoggerFactory.getLogger(WordTextEditServicePatternReplaceTest.class);

    @TempDir
    Path tempDir;

    private WordTextEditService service;
    private String testDocPath;

    @BeforeEach
    void setUp() {
        service = new WordTextEditService();
        testDocPath = tempDir.resolve("test_pattern_replace.docx").toString();
    }

    @Test
    void testHasMatchingContent_withSpecificDoc(){
        Pattern pattern =  Pattern.compile("^(合同编号|编号)\\s*[:：]\\s*$");
        String docFilePath = "/Users/yaolilin/Downloads/2025年柳州市托育服务职业技能竞赛 及2025年自治区托育竞赛赛前集训承办协议.docx";
        boolean result = service.hasMatchingContent(docFilePath, pattern);
        assertTrue(result);
    }

    @Test
    void testReplaceTextByPattern_withSpecificDoc(){
        Pattern pattern =   Pattern.compile("^\\s*(合同编号|编号)[:：]\\S+$");
        String docFilePath = "/Users/yaolilin/Downloads/重大活动线上宣传氛围营造协议.docx";
        boolean matchResult = service.hasMatchingContent(docFilePath, pattern);
        assertTrue(matchResult);

        boolean result = service.replaceTextByPattern(docFilePath, pattern, "编号：HT-2025-002",false);
        assertTrue(result);
    }

    /**
     * 测试根据正则表达式模式替换文本（Pattern版本）
     */
    @Test
    void testReplaceTextByPattern_PatternVersion() throws IOException {
        log.info("=== 测试根据正则表达式模式替换文本（Pattern版本） ===");

        // 创建测试文档
        createTestDocumentWithPatterns();

        // 测试替换所有以"HT-"开头的合同编号
        Pattern contractPattern = Pattern.compile("HT-\\d{4}-\\d{3}");
        String replacement = "HT-2025-001";

        log.info("替换模式：{}，替换为：{}", contractPattern.pattern(), replacement);

        boolean result = service.replaceTextByPattern(testDocPath, contractPattern, replacement);

        assertTrue(result, "正则表达式替换应该成功");

        // 验证替换结果
        verifyReplacementResult(replacement);

        log.info("✅ Pattern版本正则表达式替换测试通过");
    }

    /**
     * 测试根据正则表达式模式替换文本（字符串版本）
     */
    @Test
    void testReplaceTextByPattern_StringVersion() throws IOException {
        log.info("=== 测试根据正则表达式模式替换文本（字符串版本） ===");

        // 创建测试文档
        createTestDocumentWithPatterns();

        // 测试替换所有日期格式
        String patternString = "\\d{4}年";
        String replacement = "2025年";

        log.info("替换模式：{}，替换为：{}", patternString, replacement);

        boolean result = service.replaceTextByPattern(testDocPath, patternString, replacement);

        assertTrue(result, "字符串版本正则表达式替换应该成功");

        // 验证替换结果
        verifyReplacementResult(replacement);

        log.info("✅ 字符串版本正则表达式替换测试通过");
    }

    /**
     * 测试使用捕获组的正则表达式替换
     */
    @Test
    void testReplaceTextByPattern_WithCaptureGroups() throws IOException {
        log.info("=== 测试使用捕获组的正则表达式替换 ===");

        // 创建测试文档
        createTestDocumentWithCaptureGroups();

        // 测试使用捕获组保留部分原始文本
        Pattern projectPattern = Pattern.compile("PRJ-(\\d{4})-(\\d{3})");
        String replacement = "PRJ-2025-$2"; // 保留第二个捕获组（序号）

        log.info("替换模式：{}，替换为：{}", projectPattern.pattern(), replacement);

        boolean result = service.replaceTextByPattern(testDocPath, projectPattern, replacement);

        assertTrue(result, "捕获组正则表达式替换应该成功");

        // 验证替换结果
        verifyCaptureGroupReplacement();

        log.info("✅ 捕获组正则表达式替换测试通过");
    }

    /**
     * 测试复杂正则表达式替换
     */
    @Test
    void testReplaceTextByPattern_ComplexPattern() throws IOException {
        log.info("=== 测试复杂正则表达式替换 ===");

        // 创建测试文档
        createTestDocumentWithComplexPatterns();

        // 测试替换邮箱地址
        Pattern emailPattern = Pattern.compile("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
        String replacement = "$1@newcompany.com";

        log.info("替换模式：{}，替换为：{}", emailPattern.pattern(), replacement);

        boolean result = service.replaceTextByPattern(testDocPath, emailPattern, replacement);

        assertTrue(result, "复杂正则表达式替换应该成功");

        // 验证替换结果
        verifyEmailReplacement();

        log.info("✅ 复杂正则表达式替换测试通过");
    }

    /**
     * 测试无匹配项的情况
     */
    @Test
    void testReplaceTextByPattern_NoMatch() throws IOException {
        log.info("=== 测试无匹配项的情况 ===");

        // 创建测试文档
        createTestDocumentWithPatterns();

        // 测试不匹配的模式
        Pattern noMatchPattern = Pattern.compile("XYZ-\\d{4}-\\d{3}");
        String replacement = "XYZ-2025-001";

        log.info("替换模式：{}，替换为：{}", noMatchPattern.pattern(), replacement);

        boolean result = service.replaceTextByPattern(testDocPath, noMatchPattern, replacement);

        assertFalse(result, "无匹配项时应该返回false");

        log.info("✅ 无匹配项测试通过");
    }

    /**
     * 创建包含多种模式的测试文档
     */
    private void createTestDocumentWithPatterns() throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 创建标题段落
            XWPFParagraph titlePara = document.createParagraph();
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText("正则表达式替换测试文档");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // 创建段落1：包含合同编号
            XWPFParagraph para1 = document.createParagraph();
            XWPFRun run1 = para1.createRun();
            run1.setText("合同编号：HT-2024-001");

            // 创建段落2：包含另一个合同编号
            XWPFParagraph para2 = document.createParagraph();
            XWPFRun run2 = para2.createRun();
            run2.setText("项目编号：HT-2023-002");

            // 创建段落3：包含日期
            XWPFParagraph para3 = document.createParagraph();
            XWPFRun run3 = para3.createRun();
            run3.setText("创建日期：2024年12月26日");

            // 创建段落4：包含另一个日期
            XWPFParagraph para4 = document.createParagraph();
            XWPFRun run4 = para4.createRun();
            run4.setText("更新日期：2024年11月15日");

            // 保存文档
            try (FileOutputStream fos = new FileOutputStream(testDocPath)) {
                document.write(fos);
            }

            log.info("包含多种模式的测试文档创建成功：{}", testDocPath);
        }
    }

    /**
     * 创建包含捕获组的测试文档
     */
    private void createTestDocumentWithCaptureGroups() throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 创建标题段落
            XWPFParagraph titlePara = document.createParagraph();
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText("捕获组替换测试文档");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // 创建段落1：包含项目代码
            XWPFParagraph para1 = document.createParagraph();
            XWPFRun run1 = para1.createRun();
            run1.setText("项目代码：PRJ-2024-001");

            // 创建段落2：包含另一个项目代码
            XWPFParagraph para2 = document.createParagraph();
            XWPFRun run2 = para2.createRun();
            run2.setText("项目代码：PRJ-2023-002");

            // 保存文档
            try (FileOutputStream fos = new FileOutputStream(testDocPath)) {
                document.write(fos);
            }

            log.info("包含捕获组的测试文档创建成功：{}", testDocPath);
        }
    }

    /**
     * 创建包含复杂模式的测试文档
     */
    private void createTestDocumentWithComplexPatterns() throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 创建标题段落
            XWPFParagraph titlePara = document.createParagraph();
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText("复杂模式替换测试文档");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // 创建段落1：包含邮箱地址
            XWPFParagraph para1 = document.createParagraph();
            XWPFRun run1 = para1.createRun();
            run1.setText("联系邮箱：user1@oldcompany.com");

            // 创建段落2：包含另一个邮箱地址
            XWPFParagraph para2 = document.createParagraph();
            XWPFRun run2 = para2.createRun();
            run2.setText("备用邮箱：user2@oldcompany.com");

            // 保存文档
            try (FileOutputStream fos = new FileOutputStream(testDocPath)) {
                document.write(fos);
            }

            log.info("包含复杂模式的测试文档创建成功：{}", testDocPath);
        }
    }

    /**
     * 验证替换结果
     */
    private void verifyReplacementResult(String expectedText) throws IOException {
        try (FileInputStream fis = new FileInputStream(testDocPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            boolean found = false;
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text.contains(expectedText)) {
                    found = true;
                    log.info("找到替换后的文本：'{}'", text);
                    break;
                }
            }

            assertTrue(found, "文档中应该包含替换后的文本：" + expectedText);
        }
    }

    /**
     * 验证捕获组替换结果
     */
    private void verifyCaptureGroupReplacement() throws IOException {
        try (FileInputStream fis = new FileInputStream(testDocPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            boolean found = false;
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text.contains("PRJ-2025-001") || text.contains("PRJ-2025-002")) {
                    found = true;
                    log.info("找到捕获组替换后的文本：'{}'", text);
                    break;
                }
            }

            assertTrue(found, "文档中应该包含捕获组替换后的文本");
        }
    }

    /**
     * 验证邮箱替换结果
     */
    private void verifyEmailReplacement() throws IOException {
        try (FileInputStream fis = new FileInputStream(testDocPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            boolean found = false;
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text.contains("@newcompany.com")) {
                    found = true;
                    log.info("找到邮箱替换后的文本：'{}'", text);
                    break;
                }
            }

            assertTrue(found, "文档中应该包含邮箱替换后的文本");
        }
    }
}
