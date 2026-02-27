package com.customization.yll.common.doc;

import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WordTextEditService 单元测试类
 *
 * @author 姚礼林
 * @date 2025
 */
class WordTextEditServiceTest {

    private static final Logger log = LoggerFactory.getLogger(WordTextEditServiceTest.class);

    @TempDir
    Path tempDir;

    private WordTextEditService service;
    private String testDocPath;

    @BeforeEach
    void setUp() {
        service = new WordTextEditService();
        testDocPath = tempDir.resolve("test_contract.docx").toString();
    }

    @Test
    void testAddContractNumToDoc_Success() throws IOException {
        // 创建测试文档
        createTestDocument();

        // 执行测试
        String contractNum = "HT-2025-001";
        boolean result = service.addContractNumToDoc(testDocPath, contractNum);

        // 验证结果
        assertTrue(result, "合同编号插入应该成功");

        // 验证文档内容
        verifyDocumentContent(contractNum);
    }

    @Test
    void testAddContractNumToDoc_EmptyFilePath() {
        boolean result = service.addContractNumToDoc("", "HT-2025-001");
        assertFalse(result, "空文件路径应该返回false");
    }

    @Test
    void testAddContractNumToDoc_NullFilePath() {
        boolean result = service.addContractNumToDoc(null, "HT-2025-001");
        assertFalse(result, "null文件路径应该返回false");
    }

    @Test
    void testAddContractNumToDoc_EmptyContractNum() throws IOException {
        createTestDocument();
        boolean result = service.addContractNumToDoc(testDocPath, "");
        assertFalse(result, "空合同编号应该返回false");
    }

    @Test
    void testAddContractNumToDoc_NullContractNum() throws IOException {
        createTestDocument();
        boolean result = service.addContractNumToDoc(testDocPath, null);
        assertFalse(result, "null合同编号应该返回false");
    }

    @Test
    void testAddContractNumToDoc_NoMatchingPattern() throws IOException {
        // 创建不包含匹配模式的测试文档
        createTestDocumentWithoutPattern();

        String contractNum = "HT-2025-001";
        boolean result = service.addContractNumToDoc(testDocPath, contractNum);

        // 应该返回false，因为没找到匹配的模式
        assertFalse(result, "没有找到匹配模式应该返回false");
    }

    @Test
    void testAddContractNumToDoc_OnlyInsertOnce() throws IOException {
        // 创建包含多个匹配模式的测试文档
        createTestDocumentWithMultiplePatterns();

        String contractNum = "HT-2025-001";
        boolean result = service.addContractNumToDoc(testDocPath, contractNum);

        // 验证结果
        assertTrue(result, "合同编号插入应该成功");

        // 验证只插入了一次
        verifyOnlyOneInsertion(contractNum);
    }

    @Test
    void testResetInsertStatus() {
        // 这个方法在新服务中不再需要，因为不再维护插入状态
        log.info("插入状态管理在新服务中已简化，不再需要手动重置");
    }

    /**
     * 测试自定义文档
     * 这个测试方法允许您测试自己创建的文档
     * 测试完成后，您可以在指定目录查看修改后的文档
     */
    @Test
    void testCustomDocument() throws IOException {
        // 设置自定义文档路径 - 请修改为您自己的文档路径
        String customDocPath = "/Users/yaolilin/Desktop/测试.docx";

        // 检查文档是否存在
        File docFile = new File(customDocPath);
        if (!docFile.exists()) {
            log.error("❌ 文档不存在: {}", customDocPath);
            log.info("💡 请确保文档路径正确，或者先创建测试文档");
            return;
        }

        // 设置合同编号
        String contractNum = "HT-2025-CUSTOM-001";

        log.info("=== 自定义文档测试 ===");
        log.info("文档路径: {}", customDocPath);
        log.info("合同编号: {}", contractNum);
        log.info("📄 原文档大小: {} bytes", docFile.length());
        log.info("📅 原文档修改时间: {}", new java.util.Date(docFile.lastModified()));

        // 执行测试 - 直接处理您的文档，不清除内容
        log.info("🔍 开始分析文档内容...");
        boolean result = service.addContractNumToDoc(customDocPath, contractNum);

        // 验证结果
        if (result) {
            log.info("✅ 自定义文档测试成功！");
            log.info("📁 修改后的文档保存在: {}", customDocPath);
            log.info("🔍 查看文档命令: open {}", customDocPath);

            // 验证文档内容
            verifyCustomDocumentContent(customDocPath, contractNum);

            // 输出修改后的文件信息
            if (docFile.exists()) {
                log.info("📄 修改后文档大小: {} bytes", docFile.length());
                log.info("📅 修改后时间: {}", new java.util.Date(docFile.lastModified()));
            }

        } else {
            log.warn("⚠️ 自定义文档测试失败或未找到合适的插入位置");
            log.info("💡 可能的原因：");
            log.info("   1. 文档中没有包含'合同编号：'或'编号：'的行");
            log.info("   2. 这些行后面已经有其他内容");
            log.info("   3. 文档格式不支持");
        }

        log.info("=== 测试完成 ===");
        log.info("💡 提示：您可以手动打开文档查看修改结果");
        log.info("📂 文档目录: {}", docFile.getParent());

        // 分析文档内容，帮助调试
        analyzeDocumentContent(customDocPath);

        // 显示插入后的文档内容
        service.displayDocumentContent(customDocPath, "插入后");
    }

    /**
     * 测试纯文本插入功能（不修改格式）
     */
    @Test
    void testInsertTextByPattern_NoFormat() throws IOException {
        // 创建测试文档
        createTestDocument();

        // 创建自定义关键词模式
        Pattern customPattern = Pattern.compile("^(项目编号)\\s*[:：]\\s*$");
        String insertText = "PRJ-2025-001";

        // 执行测试 - 只插入文本，不修改格式
        boolean result = service.insertTextByPattern(testDocPath, customPattern, insertText,
                                                  WordTextEditService.InsertMode.FIRST_MATCH_ONLY);

        // 验证结果
        assertTrue(result, "文本插入应该成功");

        // 验证文档内容
        verifyDocumentContent(insertText);
    }

    /**
     * 测试文本插入并修改格式功能
     */
    @Test
    void testInsertTextAndFormatParagraph() throws IOException {
        // 创建测试文档
        createTestDocument();

        // 创建自定义关键词模式
        Pattern customPattern = Pattern.compile("^(项目编号)\\s*[:：]\\s*$");
        String insertText = "PRJ-2025-002";

        // 执行测试 - 插入文本并修改格式
        boolean result = service.insertTextAndFormatParagraph(testDocPath, customPattern, insertText,
                                                           WordTextEditService.InsertMode.FIRST_MATCH_ONLY,
                                                           WordTextEditService.TextAlignment.CENTER);

        // 验证结果
        assertTrue(result, "文本插入和格式修改应该成功");

        // 验证文档内容
        verifyDocumentContent(insertText);
    }

    /**
     * 测试插入所有匹配项
     */
    @Test
    void testInsertAllMatches() throws IOException {
        // 创建包含多个匹配模式的测试文档
        createTestDocumentWithMultiplePatterns();

        String contractNum = "HT-2025-ALL-001";

        // 执行测试 - 插入所有匹配项
        boolean result = service.addContractNumToDoc(testDocPath, contractNum,
                                                  WordTextEditService.InsertMode.ALL_MATCHES,
                                                  WordTextEditService.TextAlignment.RIGHT);

        // 验证结果
        assertTrue(result, "插入所有匹配项应该成功");

        // 验证文档内容
        verifyDocumentContent(contractNum);
    }

    /**
     * 分析文档内容，帮助调试匹配问题
     */
    private void analyzeDocumentContent(String docPath) throws IOException {
        log.info("🔍 === 文档内容分析 ===");

        try (FileInputStream fis = new FileInputStream(docPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            // 分析段落
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            log.info("📝 文档包含 {} 个段落", paragraphs.size());

            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);
                String text = paragraph.getText();
                String trimmedText = text.trim();

                log.info("段落 {}: '{}'", i + 1, text);
                log.info("  长度: {}, 去除空格后: '{}'", text.length(), trimmedText);

                // 检查是否匹配各种模式
                if (trimmedText.matches("^编号\\s*[:：]\\s*$")) {
                    log.info("  ✅ 匹配编号模式");
                } else if (trimmedText.matches("^(合同编号|编号)\\s*[:：]\\s*$")) {
                    log.info("  ✅ 匹配合同编号模式");
                } else {
                    log.info("  ❌ 不匹配任何模式");
                }
            }

            // 分析表格
            List<XWPFTable> tables = document.getTables();
            log.info("📊 文档包含 {} 个表格", tables.size());

            for (int i = 0; i < tables.size(); i++) {
                XWPFTable table = tables.get(i);
                log.info("表格 {}: {} 行 x {} 列", i + 1, table.getRows().size(),
                        table.getRows().isEmpty() ? 0 : table.getRow(0).getTableCells().size());

                for (int rowIndex = 0; rowIndex < table.getRows().size(); rowIndex++) {
                    XWPFTableRow row = table.getRow(rowIndex);
                    for (int colIndex = 0; colIndex < row.getTableCells().size(); colIndex++) {
                        XWPFTableCell cell = row.getTableCells().get(colIndex);
                        String cellText = cell.getText();
                        log.info("  单元格[{}, {}]: '{}'", rowIndex, colIndex, cellText);
                    }
                }
            }

        } catch (Exception e) {
            log.error("❌ 分析文档内容时发生异常", e);
        }

        log.info("🔍 === 文档内容分析完成 ===");
    }

    /**
     * 创建包含匹配模式的测试文档
     */
    private void createTestDocument() throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 创建段落1：普通内容
            XWPFParagraph para1 = document.createParagraph();
            XWPFRun run1 = para1.createRun();
            run1.setText("这是一个测试文档");

            // 创建段落2：包含"合同编号："的行
            XWPFParagraph para2 = document.createParagraph();
            XWPFRun run2 = para2.createRun();
            run2.setText("合同编号：");

            // 创建段落3：包含"项目编号："的行
            XWPFParagraph para3 = document.createParagraph();
            XWPFRun run3 = para3.createRun();
            run3.setText("项目编号：");

            // 创建段落4：其他内容
            XWPFParagraph para4 = document.createParagraph();
            XWPFRun run4 = para4.createRun();
            run4.setText("文档的其他内容");

            // 保存文档
            try (FileOutputStream fos = new FileOutputStream(testDocPath)) {
                document.write(fos);
            }
        }
    }

    /**
     * 创建不包含匹配模式的测试文档
     */
    private void createTestDocumentWithoutPattern() throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 创建段落1：普通内容
            XWPFParagraph para1 = document.createParagraph();
            XWPFRun run1 = para1.createRun();
            run1.setText("这是一个测试文档");

            // 创建段落2：不匹配的内容
            XWPFParagraph para2 = document.createParagraph();
            XWPFRun run2 = para2.createRun();
            run2.setText("合同编号：HT-2024-001");

            // 保存文档
            try (FileOutputStream fos = new FileOutputStream(testDocPath)) {
                document.write(fos);
            }
        }
    }

    /**
     * 创建包含多个匹配模式的测试文档
     */
    private void createTestDocumentWithMultiplePatterns() throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 创建段落1：普通内容
            XWPFParagraph para1 = document.createParagraph();
            XWPFRun run1 = para1.createRun();
            run1.setText("这是一个测试文档");

            // 创建段落2：包含"合同编号："的行
            XWPFParagraph para2 = document.createParagraph();
            XWPFRun run2 = para2.createRun();
            run2.setText("合同编号：");

            // 创建段落3：包含"编号："的行
            XWPFParagraph para3 = document.createParagraph();
            XWPFRun run3 = para3.createRun();
            run3.setText("编号：");

            // 保存文档
            try (FileOutputStream fos = new FileOutputStream(testDocPath)) {
                document.write(fos);
            }
        }
    }

    /**
     * 创建包含项目编号的测试文档
     */
    private void createTestDocumentWithProjectCode() throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 创建段落1：普通内容
            XWPFParagraph para1 = document.createParagraph();
            XWPFRun run1 = para1.createRun();
            run1.setText("这是一个测试文档");

            // 创建段落2：包含"项目编号："的行
            XWPFParagraph para2 = document.createParagraph();
            XWPFRun run2 = para2.createRun();
            run2.setText("项目编号：");

            // 创建段落3：其他内容
            XWPFParagraph para3 = document.createParagraph();
            XWPFRun run3 = para3.createRun();
            run3.setText("文档的其他内容");

            // 保存文档
            try (FileOutputStream fos = new FileOutputStream(testDocPath)) {
                document.write(fos);
            }
        }
    }

    /**
     * 验证文档内容
     */
    private void verifyDocumentContent(String expectedText) throws IOException {
        try (FileInputStream fis = new FileInputStream(testDocPath);
             XWPFDocument document = new XWPFDocument(fis)) {
            boolean found = false;

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                // 检查多种可能的格式
                if (text.contains("合同编号：" + expectedText) ||
                    text.contains("编号：" + expectedText) ||
                    text.contains("项目编号：" + expectedText) ||
                    text.contains("项目代码：" + expectedText) ||
                    text.contains(expectedText)) {
                    found = true;
                    log.info("找到匹配的文本: '{}'", text);
                    break;
                }
            }

            assertTrue(found, "文档中应该包含插入的文本: " + expectedText);
        }
    }

    /**
     * 验证只插入了一次
     */
    private void verifyOnlyOneInsertion(String expectedContractNum) throws IOException {
        try (FileInputStream fis = new FileInputStream(testDocPath);
             XWPFDocument document = new XWPFDocument(fis)) {
            int count = 0;

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text.contains("合同编号：" + expectedContractNum)) {
                    count++;
                }
            }

            assertEquals(1, count, "合同编号应该只插入一次");
        }
    }

    /**
     * 创建自定义测试文档
     * 这个方法创建一个包含多种合同编号模式的测试文档
     */
    private void createCustomTestDocument(String docPath) throws IOException {
        // 确保目录存在
        File parentDir = new File(docPath).getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (XWPFDocument document = new XWPFDocument()) {
            // 创建标题段落
            XWPFParagraph titlePara = document.createParagraph();
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText("合同文档测试");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // 创建段落1：普通内容
            XWPFParagraph para1 = document.createParagraph();
            XWPFRun run1 = para1.createRun();
            run1.setText("这是一个用于测试合同编号插入功能的文档。");

            // 创建段落2：包含"合同编号："的行（应该被匹配）
            XWPFParagraph para2 = document.createParagraph();
            XWPFRun run2 = para2.createRun();
            run2.setText("合同编号：");

            // 创建段落3：包含"编号："的行（应该被匹配）
            XWPFParagraph para3 = document.createParagraph();
            XWPFRun run3 = para3.createRun();
            run3.setText("编号：");

            // 创建段落4：不匹配的内容
            XWPFParagraph para4 = document.createParagraph();
            XWPFRun run4 = para4.createRun();
            run4.setText("合同编号：HT-2024-001"); // 已包含编号，不匹配

            // 创建段落5：其他内容
            XWPFParagraph para5 = document.createParagraph();
            XWPFRun run5 = para5.createRun();
            run5.setText("文档的其他内容，用于测试功能完整性。");

            // 创建表格
            XWPFTable table = document.createTable(2, 2);

            // 表格第一行
            XWPFTableRow row1 = table.getRow(0);
            row1.getCell(0).setText("字段名");
            row1.getCell(1).setText("字段值");

            // 表格第二行
            XWPFTableRow row2 = table.getRow(1);
            row2.getCell(0).setText("合同编号：");
            row2.getCell(1).setText("待填写");

            // 保存文档
            try (FileOutputStream fos = new FileOutputStream(docPath)) {
                document.write(fos);
            }

            log.info("自定义测试文档创建成功: {}", docPath);
        }
    }

    /**
     * 验证自定义文档内容
     */
    private void verifyCustomDocumentContent(String docPath, String expectedContractNum) throws IOException {
        try (FileInputStream fis = new FileInputStream(docPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            boolean found = false;
            int matchCount = 0;

            // 检查段落
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                log.debug("检查段落: '{}'", text);

                // 检查两种可能的格式
                if (text.contains("合同编号：" + expectedContractNum)) {
                    found = true;
                    matchCount++;
                    log.info("✅ 在段落中找到合同编号: {}", text);
                } else if (text.contains("编号：" + expectedContractNum)) {
                    found = true;
                    matchCount++;
                    log.info("✅ 在段落中找到编号: {}", text);
                }
            }

            // 检查表格
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            String text = paragraph.getText();
                            log.debug("检查表格单元格: '{}'", text);

                            // 检查两种可能的格式
                            if (text.contains("合同编号：" + expectedContractNum)) {
                                found = true;
                                matchCount++;
                                log.info("✅ 在表格中找到合同编号: {}", text);
                            } else if (text.contains("编号：" + expectedContractNum)) {
                                found = true;
                                matchCount++;
                                log.info("✅ 在表格中找到编号: {}", text);
                            }
                        }
                    }
                }
            }

            if (found) {
                log.info("✅ 文档内容验证通过，找到 {} 个合同编号", matchCount);
                assertTrue(matchCount >= 1, "应该至少找到一个合同编号");
            } else {
                log.warn("⚠️ 文档内容验证失败，未找到合同编号");
                log.info("💡 正在搜索的合同编号: {}", expectedContractNum);
                log.info("💡 支持的格式: '合同编号：{}' 或 '编号：{}'", expectedContractNum, expectedContractNum);

                // 输出文档内容以便调试
                log.info("🔍 文档内容分析:");
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    String text = paragraph.getText();
                    if (!text.trim().isEmpty()) {
                        log.info("  段落: '{}'", text);
                    }
                }

                fail("文档中应该包含插入的合同编号");
            }
        }
    }

    /**
     * 测试文本替换功能
     */
    @Test
    void testTextReplacement() throws IOException {
        // 创建测试文档
        createTestDocument();

        // 先插入一个合同编号
        String oldContractNum = "HT-2024-001";
        boolean insertResult = service.addContractNumToDoc(testDocPath, oldContractNum);
        assertTrue(insertResult, "应该能够插入合同编号");

        // 验证插入成功
        verifyDocumentContent(oldContractNum);

        // 现在替换合同编号
        String newContractNum = "HT-2025-001";
        boolean replaceResult = service.replaceContractNum(testDocPath, oldContractNum, newContractNum);
        assertTrue(replaceResult, "应该能够替换合同编号");

        // 验证替换成功
        verifyDocumentContent(newContractNum);

        // 验证旧编号不再存在
        try (FileInputStream fis = new FileInputStream(testDocPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            boolean oldFound = false;
            boolean newFound = false;

            // 检查段落
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text.contains(oldContractNum)) {
                    oldFound = true;
                }
                if (text.contains(newContractNum)) {
                    newFound = true;
                }
            }

            // 检查表格
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            String text = paragraph.getText();
                            if (text.contains(oldContractNum)) {
                                oldFound = true;
                            }
                            if (text.contains(newContractNum)) {
                                newFound = true;
                            }
                        }
                    }
                }
            }

            assertFalse(oldFound, "旧合同编号应该被完全替换");
            assertTrue(newFound, "新合同编号应该存在");
        }
    }

    /**
     * 测试通用文本替换功能
     */
    @Test
    void testReplaceText() throws IOException {
        // 创建包含项目编号的测试文档
        createTestDocumentWithProjectCode();

        // 先插入一些文本
        Pattern pattern = Pattern.compile("^(项目编号)\\s*[:：]\\s*$");
        String oldText = "PRJ-2024-001";
        boolean insertResult = service.insertTextByPattern(testDocPath, pattern, oldText,
                                                        WordTextEditService.InsertMode.FIRST_MATCH_ONLY);
        assertTrue(insertResult, "应该能够插入项目编号");

        // 验证插入成功 - 检查段落是否包含插入的文本
        try (FileInputStream fis = new FileInputStream(testDocPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            boolean inserted = false;
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text.contains(oldText)) {
                    inserted = true;
                    log.info("找到插入的文本: {}", text);
                    break;
                }
            }
            assertTrue(inserted, "应该能够找到插入的文本");
        }

        // 现在替换文本
        String newText = "PRJ-2025-001";
        boolean replaceResult = service.replaceText(testDocPath, oldText, newText);
        assertTrue(replaceResult, "应该能够替换文本");

        // 验证替换成功
        try (FileInputStream fis = new FileInputStream(testDocPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            boolean oldFound = false;
            boolean newFound = false;

            // 检查段落
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                log.debug("检查段落: '{}'", text);
                if (text.contains(oldText)) {
                    oldFound = true;
                    log.warn("仍然找到旧文本: {}", text);
                }
                if (text.contains(newText)) {
                    newFound = true;
                    log.info("找到新文本: {}", text);
                }
            }

            assertFalse(oldFound, "旧文本应该被完全替换");
            assertTrue(newFound, "新文本应该存在");
        }
    }

    /**
     * 测试自定义文档的文本替换功能
     * 这个方法允许您测试自己创建的文档
     */
    @Test
    void testCustomDocumentTextReplacement() throws IOException {
        log.info("\n=== 测试自定义文档的文本替换功能 ===");

        // 指定您要测试的文档路径
        String customDocPath = "/Users/yaolilin/Desktop/测试.docx"; // 请修改为您的文档路径

        // 检查文档是否存在
        File customDoc = new File(customDocPath);
        if (!customDoc.exists()) {
            log.warn("⚠️ 自定义文档不存在: {}", customDocPath);
            log.info("💡 请将您的测试文档放在指定路径，或修改 customDocPath 变量");
            log.info("💡 建议路径: /Users/yaolilin/Desktop/test_contract.docx");
            return;
        }

        log.info("✅ 找到自定义文档: {}", customDocPath);

        // 显示文档原始内容
        log.info("📄 文档原始内容:");
        service.displayDocumentContent(customDocPath, "原始状态");

        // 定义要替换的文本
        String oldText = "编号：HT-2025-CUSTOM-001";  // 请修改为您要替换的旧文本
        String newText = "编号：HT-2025-CUSTOM-002";  // 请修改为您要替换的新文本

        log.info("🔄 准备替换文本: {} -> {}", oldText, newText);

        // 检查文档是否包含要替换的文本
        boolean hasOldText = service.hasMatchingContent(customDocPath,
            Pattern.compile(".*" + Pattern.quote(oldText) + ".*"));

        if (!hasOldText) {
            log.warn("⚠️ 文档中未找到要替换的文本: {}", oldText);
            log.info("💡 请检查 oldText 变量，确保文档中包含此文本");
            log.info("💡 或者先使用 insertTextByPattern 方法插入文本，再进行替换");
            return;
        }

        log.info("✅ 文档中包含要替换的文本: {}", oldText);

        // 执行文本替换
        log.info("🔄 开始执行文本替换...");
        boolean replaceResult = service.replaceText(customDocPath, oldText, newText);

        if (replaceResult) {
            log.info("✅ 文本替换成功！");

            // 显示替换后的文档内容
            log.info("📄 替换后的文档内容:");
            service.displayDocumentContent(customDocPath, "替换后");

            // 验证替换结果
            log.info("🔍 验证替换结果...");
            verifyReplacementResult(customDocPath, oldText, newText);

            // 显示文档路径，方便查看
            log.info("📁 文档已保存到: {}", customDocPath);
            log.info("💡 您现在可以打开文档查看效果");

            // 尝试自动打开文档（macOS）
            try {
                ProcessBuilder pb = new ProcessBuilder("open", customDocPath);
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("🚀 已自动打开文档，请查看替换效果");
                } else {
                    log.info("💡 请手动打开文档查看效果: {}", customDocPath);
                }
            } catch (Exception e) {
                log.info("💡 请手动打开文档查看效果: {}", customDocPath);
            }

        } else {
            log.error("❌ 文本替换失败");
            log.info("💡 请检查文档内容和替换参数");
        }
    }

    /**
     * 验证替换结果
     */
    private void verifyReplacementResult(String docPath, String oldText, String newText) throws IOException {
        try (FileInputStream fis = new FileInputStream(docPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            boolean oldFound = false;
            boolean newFound = false;
            int oldCount = 0;
            int newCount = 0;

            // 检查段落
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text.contains(oldText)) {
                    oldFound = true;
                    oldCount++;
                    log.warn("⚠️ 仍然找到旧文本: {}", text);
                }
                if (text.contains(newText)) {
                    newFound = true;
                    newCount++;
                    log.info("✅ 找到新文本: {}", text);
                }
            }

            // 检查表格
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            String text = paragraph.getText();
                            if (text.contains(oldText)) {
                                oldFound = true;
                                oldCount++;
                                log.warn("⚠️ 表格中找到旧文本: {}", text);
                            }
                            if (text.contains(newText)) {
                                newFound = true;
                                newCount++;
                                log.info("✅ 表格中找到新文本: {}", text);
                            }
                        }
                    }
                }
            }

            // 输出验证结果
            if (!oldFound && newFound) {
                log.info("🎉 替换验证成功！");
                log.info("   - 旧文本 '{}' 已完全替换", oldText);
                log.info("   - 新文本 '{}' 已正确插入", newText);
                log.info("   - 新文本出现次数: {}", newCount);
            } else if (oldFound) {
                log.warn("⚠️ 替换验证失败！");
                log.warn("   - 旧文本 '{}' 仍然存在 ({} 次)", oldText, oldCount);
                log.warn("   - 新文本 '{}' 出现次数: {}", newText, newCount);
            } else {
                log.error("❌ 替换验证失败！");
                log.error("   - 旧文本 '{}' 未找到", oldText);
                log.error("   - 新文本 '{}' 也未找到", newText);
            }
        }
    }
}
