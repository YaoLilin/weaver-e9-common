package com.customization.yll.common.doc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * WordTextInsertService 使用示例
 *
 * @author 姚礼林
 * @desc 展示如何使用WordTextInsertService的各种功能
 * @date 2025/8/13
 **/
public class WordTextInsertServiceExample {

    private static final Logger log = LoggerFactory.getLogger(WordTextInsertServiceExample.class);

    public static void main(String[] args) {
        WordTextInsertServiceExample example = new WordTextInsertServiceExample();
        example.runExamples();
    }

    /**
     * 运行所有示例
     */
    public void runExamples() {
        log.info("=== WordTextInsertService 使用示例 ===");

        // 示例1：基本合同编号插入
        example1_BasicContractNumInsert();

        // 示例2：自定义关键词模式插入
        example2_CustomKeywordPattern();

        // 示例3：插入所有匹配项
        example3_InsertAllMatches();

        // 示例4：自定义对齐方式
        example4_CustomAlignment();

        // 示例5：检查文档内容
        example5_CheckDocumentContent();

        // 示例6：文本替换功能
        example6_TextReplacement();

        log.info("=== 所有示例运行完成 ===");
    }

    /**
     * 示例1：基本合同编号插入
     */
    private void example1_BasicContractNumInsert() {
        log.info("\n--- 示例1：基本合同编号插入 ---");

        WordTextEditService service = new WordTextEditService();

        String docPath = "/path/to/your/document.docx";
        String contractNum = "HT-2025-001";

        log.info("插入合同编号: {}", contractNum);

        boolean success = service.addContractNumToDoc(docPath, contractNum);

        if (success) {
            log.info("✅ 合同编号插入成功");
        } else {
            log.warn("⚠️ 合同编号插入失败");
        }
    }

    /**
     * 示例2：自定义关键词模式插入
     */
    private void example2_CustomKeywordPattern() {
        log.info("\n--- 示例2：自定义关键词模式插入 ---");

        // 创建自定义关键词模式：匹配"项目编号："或"项目代码："
        Pattern customPattern = Pattern.compile("^(项目编号|项目代码)\\s*[:：]\\s*$");

        WordTextEditService service = new WordTextEditService();

        String docPath = "/path/to/your/project_doc.docx";
        String projectCode = "PRJ-2025-001";

        log.info("使用自定义模式插入项目代码: {}", projectCode);
        log.info("关键词模式: {}", customPattern.pattern());

        boolean success = service.insertTextByPattern(docPath, customPattern, projectCode,
                                                   WordTextEditService.InsertMode.FIRST_MATCH_ONLY);

        if (success) {
            log.info("✅ 项目代码插入成功");
        } else {
            log.warn("⚠️ 项目代码插入失败");
        }
    }

    /**
     * 示例3：插入所有匹配项
     */
    private void example3_InsertAllMatches() {
        log.info("\n--- 示例3：插入所有匹配项 ---");

        WordTextEditService service = new WordTextEditService();

        String docPath = "/path/to/your/multi_match_doc.docx";
        String contractNum = "HT-2025-002";

        log.info("插入合同编号到所有匹配位置: {}", contractNum);

        boolean success = service.addContractNumToDoc(docPath, contractNum,
                                                   WordTextEditService.InsertMode.ALL_MATCHES,
                                                   WordTextEditService.TextAlignment.RIGHT);

        if (success) {
            log.info("✅ 合同编号插入到所有匹配位置成功");
        } else {
            log.warn("⚠️ 合同编号插入失败");
        }
    }

    /**
     * 示例4：自定义对齐方式
     */
    private void example4_CustomAlignment() {
        log.info("\n--- 示例4：自定义对齐方式 ---");

        WordTextEditService service = new WordTextEditService();

        String docPath = "/path/to/your/alignment_doc.docx";
        String contractNum = "HT-2025-003";

        log.info("插入合同编号（居中对齐）: {}", contractNum);

        boolean success = service.addContractNumToDoc(docPath, contractNum,
                                                   WordTextEditService.InsertMode.FIRST_MATCH_ONLY,
                                                   WordTextEditService.TextAlignment.CENTER);

        if (success) {
            log.info("✅ 合同编号插入成功（居中对齐）");
        } else {
            log.warn("⚠️ 合同编号插入失败");
        }
    }

    /**
     * 示例5：检查文档内容
     */
    private void example5_CheckDocumentContent() {
        log.info("\n--- 示例5：检查文档内容 ---");

        WordTextEditService service = new WordTextEditService();

        String docPath = "/path/to/your/check_doc.docx";
        String contractNum = "HT-2025-004";

        log.info("检查文档内容...");

        // 检查是否包含合同编号关键词
        boolean hasKeyword = service.hasMatchingContent(docPath,
            Pattern.compile("^(合同编号|编号)\\s*[:：]\\s*$"));
        log.info("文档是否包含合同编号关键词: {}", hasKeyword);

        // 检查是否已经插入了合同编号
        boolean hasInserted = service.hasMatchingContent(docPath,
            Pattern.compile(".*" + contractNum + ".*"));
        log.info("文档是否已经插入了合同编号 {}: {}", contractNum, hasInserted);

        // 如果包含关键词但未插入，则插入
        if (hasKeyword && !hasInserted) {
            log.info("文档包含关键词但未插入合同编号，开始插入...");

            boolean success = service.addContractNumToDoc(docPath, contractNum);

            if (success) {
                log.info("✅ 合同编号插入成功");

                // 再次检查是否插入成功
                boolean checkAgain = service.hasMatchingContent(docPath,
                    Pattern.compile(".*" + contractNum + ".*"));
                log.info("插入后检查结果: {}", checkAgain);
            } else {
                log.warn("⚠️ 合同编号插入失败");
            }
        } else if (hasInserted) {
            log.info("✅ 文档已经包含合同编号，无需重复插入");
        } else {
            log.warn("⚠️ 文档不包含合同编号关键词，无法插入");
        }
    }

    /**
     * 示例6：高级用法 - 批量处理多个文档
     */
    public void example6_BatchProcessDocuments() {
        log.info("\n--- 示例6：批量处理多个文档 ---");

        String[] docPaths = {
            "/path/to/doc1.docx",
            "/path/to/doc2.docx",
            "/path/to/doc3.docx"
        };

        String contractNum = "HT-2025-BATCH-001";

        WordTextEditService service = new WordTextEditService();

        int successCount = 0;
        int totalCount = docPaths.length;

        for (String docPath : docPaths) {
            log.info("处理文档: {}", docPath);

            try {
                // 检查是否已经插入
                if (service.hasMatchingContent(docPath, Pattern.compile(".*" + contractNum + ".*"))) {
                    log.info("文档已包含合同编号，跳过: {}", docPath);
                    continue;
                }

                // 插入合同编号
                boolean success = service.addContractNumToDoc(docPath, contractNum);

                if (success) {
                    successCount++;
                    log.info("✅ 文档处理成功: {}", docPath);
                } else {
                    log.warn("⚠️ 文档处理失败: {}", docPath);
                }

            } catch (Exception e) {
                log.error("处理文档时发生异常: {}", docPath, e);
            }
        }

        log.info("批量处理完成: 成功 {}/{}", successCount, totalCount);
    }

    /**
     * 示例7：创建自定义服务实例
     */
    public void example7_CustomServiceInstance() {
        log.info("\n--- 示例7：创建自定义服务实例 ---");

        // 创建自定义关键词模式：匹配"订单号："或"订单编号："
        Pattern orderPattern = Pattern.compile("^(订单号|订单编号)\\s*[:：]\\s*$");

                WordTextEditService service = new WordTextEditService();

        String docPath = "/path/to/your/order_doc.docx";
        String orderNumber = "ORD-2025-001";

        log.info("使用自定义模式插入订单号: {}", orderNumber);
        log.info("关键词模式: {}", orderPattern.pattern());

        boolean success = service.insertTextByPattern(docPath, orderPattern, orderNumber,
                                                   WordTextEditService.InsertMode.ALL_MATCHES);

        if (success) {
            log.info("✅ 订单号插入成功");
        } else {
            log.warn("⚠️ 订单号插入失败");
        }
    }

    /**
     * 示例6：文本替换功能
     */
    private void example6_TextReplacement() {
        log.info("\n--- 示例6：文本替换功能 ---");

        WordTextEditService service = new WordTextEditService();

        String docPath = "/path/to/your/contract_doc.docx";
        String oldContractNum = "HT-2024-001";
        String newContractNum = "HT-2025-001";

        log.info("替换合同编号: {} -> {}", oldContractNum, newContractNum);

        // 方法1：使用通用替换方法
        boolean success1 = service.replaceText(docPath, oldContractNum, newContractNum);

        if (success1) {
            log.info("✅ 通用文本替换成功");
        } else {
            log.warn("⚠️ 通用文本替换失败");
        }

        // 方法2：使用专门的合同编号替换方法
        boolean success2 = service.replaceContractNum(docPath, oldContractNum, newContractNum);

        if (success2) {
            log.info("✅ 合同编号替换成功");
        } else {
            log.warn("⚠️ 合同编号替换失败");
        }

        // 方法3：替换其他类型的文本
        String oldProjectCode = "PRJ-2024-001";
        String newProjectCode = "PRJ-2025-001";

        log.info("替换项目代码: {} -> {}", oldProjectCode, newProjectCode);

        boolean success3 = service.replaceText(docPath, oldProjectCode, newProjectCode);

        if (success3) {
            log.info("✅ 项目代码替换成功");
        } else {
            log.warn("⚠️ 项目代码替换失败");
        }

        // 方法4：批量替换多个文档
        String[] docPaths = {
            "/path/to/contract1.docx",
            "/path/to/contract2.docx",
            "/path/to/contract3.docx"
        };

        log.info("批量替换 {} 个文档中的合同编号", docPaths.length);

        int successCount = 0;
        for (String path : docPaths) {
            try {
                if (service.replaceContractNum(path, oldContractNum, newContractNum)) {
                    successCount++;
                    log.info("✅ 文档 {} 替换成功", path);
                } else {
                    log.warn("⚠️ 文档 {} 替换失败", path);
                }
            } catch (Exception e) {
                log.error("处理文档 {} 时发生异常", path, e);
            }
        }

        log.info("批量替换完成: 成功 {}/{}", successCount, docPaths.length);
    }
}
