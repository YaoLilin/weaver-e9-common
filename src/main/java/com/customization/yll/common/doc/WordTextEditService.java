package com.customization.yll.common.doc;

import com.customization.yll.common.IntegrationLog;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Word文档文本编辑服务
 *
 * 这是一个功能强大的Word文档文本编辑工具，提供以下核心功能：
 * 1. 文本插入：根据关键词模式在指定位置插入文本
 * 2. 文本替换：替换文档中已存在的指定文本
 * 3. 格式控制：支持文本对齐、间距等格式设置
 * 4. 批量处理：支持处理多个文档
 *
 * 主要应用场景：
 * - 合同编号插入和更新
 * - 项目代码维护
 * - 文档模板填充
 * - 批量文档处理
 *
 * 技术特点：
 * - 基于Apache POI实现，支持.docx格式
 * - 支持段落和表格中的文本操作
 * - 提供灵活的插入模式（首次匹配/全部匹配）
 * - 支持多种文本对齐方式
 * - 完善的错误处理和日志记录
 *
 * @author 姚礼林 AI
 * @version 2.0
 * @since 2024-12-26
 */
public class WordTextEditService {

    /**
     * 集成日志记录器
     *
     * 用于记录服务运行过程中的各种信息，包括：
     * - 操作开始和结束
     * - 错误和异常信息
     * - 调试和跟踪信息
     * - 性能统计信息
     */
    private static final IntegrationLog log = new IntegrationLog(WordTextEditService.class);

    /**
     * 默认的合同编号关键词模式
     *
     * 这是一个预定义的正则表达式模式，用于匹配文档中的合同编号关键词。
     * 模式说明：
     * - ^ 表示行首
     * - (合同编号|编号) 匹配"合同编号"或"编号"
     * - \\s* 匹配零个或多个空白字符
     * - [:：] 匹配冒号（支持中英文冒号）
     * - \\s* 匹配零个或多个空白字符
     * - $ 表示行尾
     *
     * 匹配示例：
     * - "合同编号：" ✓
     * - "编号：" ✓
     * - "合同编号: " ✓
     * - "编号: " ✓
     * - "合同编号：HT-2025-001" ✗ (包含额外内容)
     */
    private static final Pattern DEFAULT_CONTRACT_NUM_PATTERN = Pattern.compile("^(合同编号|编号)\\s*[:：]\\s*$");

    /**
     * 文本插入模式枚举
     *
     * 定义在文档中插入文本时的行为模式，控制是否插入所有匹配项。
     */
    public enum InsertMode {
        /**
         * 只插入第一个匹配项
         *
         * 在文档中找到第一个匹配指定模式的段落或表格单元格后，
         * 只在该位置插入文本，然后停止搜索。
         *
         * 适用场景：
         * - 合同编号只需要插入一次
         * - 避免在多个位置重复插入相同内容
         * - 提高处理性能
         */
        FIRST_MATCH_ONLY,

        /**
         * 插入所有匹配项
         *
         * 在文档中找到所有匹配指定模式的段落或表格单元格，
         * 在每个位置都插入指定的文本。
         *
         * 适用场景：
         * - 需要在多个位置插入相同内容
         * - 表格中多个单元格都需要填充
         * - 批量处理文档内容
         */
        ALL_MATCHES
    }

    /**
     * 文本对齐方式枚举
     *
     * 定义插入文本后的段落对齐方式，提供多种对齐选项。
     */
    public enum TextAlignment {
        /**
         * 左对齐
         *
         * 文本靠左对齐，适用于：
         * - 正文内容
         * - 列表项
         * - 一般文档内容
         */
        LEFT(ParagraphAlignment.LEFT),

        /**
         * 居中对齐
         *
         * 文本居中对齐，适用于：
         * - 标题
         * - 表格标题
         * - 需要突出显示的内容
         */
        CENTER(ParagraphAlignment.CENTER),

        /**
         * 右对齐
         *
         * 文本靠右对齐，适用于：
         * - 合同编号
         * - 日期信息
         * - 签名区域
         */
        RIGHT(ParagraphAlignment.RIGHT),

        /**
         * 两端对齐
         *
         * 文本两端对齐，适用于：
         * - 正式文档
         * - 需要整齐排版的内容
         * - 专业报告
         */
        BOTH(ParagraphAlignment.BOTH);

        private final ParagraphAlignment alignment;

        TextAlignment(ParagraphAlignment alignment) {
            this.alignment = alignment;
        }

        public ParagraphAlignment getAlignment() {
            return alignment;
        }
    }

    /**
     * 服务配置选项
     *
     * 这些字段用于配置服务的行为，可以通过setter方法进行设置。
     * 如果不设置，服务将使用默认值。
     */

    /**
     * 关键词匹配模式
     *
     * 用于匹配文档中需要插入文本的位置的正则表达式模式。
     * 如果为null，将使用默认的合同编号模式。
     */
    private Pattern keywordPattern;

    /**
     * 文本插入模式
     *
     * 控制是否插入所有匹配项，还是只插入第一个匹配项。
     * 如果为null，将使用默认的FIRST_MATCH_ONLY模式。
     */
    private InsertMode insertMode;

    /**
     * 文本对齐方式
     *
     * 控制插入文本后的段落对齐方式。
     * 如果为null，将使用默认的RIGHT（右对齐）方式。
     */
    private TextAlignment textAlignment;

    /**
     * 是否移除前导空格
     *
     * 在处理段落时，是否移除段落开头的空白字符。
     * 默认为false，保持原有格式。
     */
    private boolean removeLeadingSpaces;

    /**
     * 是否移除尾随空格
     *
     * 在处理段落时，是否移除段落结尾的空白字符。
     * 默认为false，保持原有格式。
     */
    private boolean removeTrailingSpaces;

    /**
     * 默认构造函数
     *
     * 创建一个新的WordTextEditService实例，使用默认配置：
     * - 插入模式：FIRST_MATCH_ONLY（只插入第一个匹配项）
     * - 文本对齐：RIGHT（右对齐）
     * - 关键词模式：默认合同编号模式
     */
    public WordTextEditService() {

    }


    /**
     * 根据关键词模式在Word文档中插入文本（不修改格式）
     *
     * 此方法会在文档中查找匹配指定模式的段落或表格单元格，并在其后插入指定文本。
     * 插入过程中保持原有文档格式不变，只添加新文本。
     *
     * 使用场景：
     * - 在"项目编号："后插入具体的项目编号
     * - 在"合同编号："后插入合同编号
     * - 在表格中的特定字段后插入数据
     *
     * 注意事项：
     * - 只支持.docx格式的Word文档
     * - 关键词模式使用正则表达式匹配
     * - 插入位置在匹配文本的末尾
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param keywordPattern 关键词匹配模式（正则表达式）
     * @param insertText 要插入的文本内容
     * @param insertMode 插入模式：FIRST_MATCH_ONLY（只插入第一个）或ALL_MATCHES（插入所有匹配项）
     * @return true 如果插入成功，false 如果插入失败或未找到匹配项
     *
     * @throws RuntimeException 如果文档读取或保存过程中发生异常
     *
     * @example
     * <pre>
     * WordTextEditService service = new WordTextEditService();
     * Pattern pattern = Pattern.compile("^(项目编号)\\s*[:：]\\s*$");
     * boolean success = service.insertTextByPattern("/path/to/doc.docx", pattern, "PRJ-2025-001", InsertMode.FIRST_MATCH_ONLY);
     * </pre>
     */
    public boolean insertTextByPattern(String docFilePath, Pattern keywordPattern, String insertText,
                                     InsertMode insertMode) {
        if (docFilePath == null || docFilePath.trim().isEmpty()) {
            log.error("文档文件路径不能为空");
            return false;
        }

        if (keywordPattern == null) {
            log.error("关键词模式不能为空");
            return false;
        }

        if (insertText == null || insertText.trim().isEmpty()) {
            log.error("插入文本不能为空");
            return false;
        }

        try {
            log.info("开始处理文档：{}，关键词模式：{}，插入文本：{}", docFilePath, keywordPattern.pattern(), insertText);

            // 读取文档
            XWPFDocument document = readDocument(docFilePath);
            if (document == null) {
                return false;
            }

            // 处理文档内容（不修改格式）
            boolean success = processDocumentForInsert(document, keywordPattern, insertText, insertMode);
            if (!success) {
                log.warn("文档处理完成，但未找到合适的插入位置");
                return false;
            }

            // 保存文档
            success = saveDocument(document, docFilePath);
            if (success) {
                log.info("文本插入成功，文档已保存");
            }

            return success;

        } catch (Exception e) {
            log.error("插入文本时发生异常", e);
            return false;
        }
    }

    /**
     * 根据关键词模式在Word文档中插入文本并修改段落格式
     *
     * 此方法会在文档中查找匹配指定模式的段落或表格单元格，插入指定文本，
     * 并应用指定的格式设置（对齐方式、间距等）。
     *
     * 与 insertTextByPattern 的区别：
     * - 此方法会清除原有段落内容，重新创建格式化的文本
     * - 支持设置文本对齐方式（左对齐、居中、右对齐、两端对齐）
     * - 自动设置段落间距为0，避免换行问题
     *
     * 使用场景：
     * - 插入合同编号并设置为右对齐
     * - 插入项目信息并设置为居中对齐
     * - 需要特定格式要求的文档内容插入
     *
     * 注意事项：
     * - 会清除原有段落的格式和内容
     * - 新文本格式为：关键词 + 插入文本
     * - 支持段落和表格单元格的处理
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param keywordPattern 关键词匹配模式（正则表达式）
     * @param insertText 要插入的文本内容
     * @param insertMode 插入模式：FIRST_MATCH_ONLY（只插入第一个）或ALL_MATCHES（插入所有匹配项）
     * @param textAlignment 文本对齐方式
     * @return true 如果插入成功，false 如果插入失败或未找到匹配项
     *
     * @throws RuntimeException 如果文档读取或保存过程中发生异常
     *
     * @example
     * <pre>
     * WordTextEditService service = new WordTextEditService();
     * Pattern pattern = Pattern.compile("^(合同编号)\\s*[:：]\\s*$");
     * boolean success = service.insertTextAndFormatParagraph("/path/to/contract.docx", pattern,
     *     "HT-2025-001", InsertMode.FIRST_MATCH_ONLY, TextAlignment.RIGHT);
     * </pre>
     */
    public boolean insertTextAndFormatParagraph(String docFilePath, Pattern keywordPattern, String insertText,
                                              InsertMode insertMode, TextAlignment textAlignment) {
        if (docFilePath == null || docFilePath.trim().isEmpty()) {
            log.error("文档文件路径不能为空");
            return false;
        }

        if (keywordPattern == null) {
            log.error("关键词模式不能为空");
            return false;
        }

        if (insertText == null || insertText.trim().isEmpty()) {
            log.error("插入文本不能为空");
            return false;
        }

        try {
            log.info("开始处理文档：{}，关键词模式：{}，插入文本：{}，对齐方式：{}",
                    docFilePath, keywordPattern.pattern(), insertText, textAlignment);

            // 读取文档
            XWPFDocument document = readDocument(docFilePath);
            if (document == null) {
                return false;
            }

            // 处理文档内容（修改格式）
            boolean success = processDocumentWithFormat(document, keywordPattern, insertText, insertMode, textAlignment);
            if (!success) {
                log.warn("文档处理完成，但未找到合适的插入位置");
                return false;
            }

            // 保存文档
            success = saveDocument(document, docFilePath);
            if (success) {
                log.info("文本插入和格式修改成功，文档已保存");
            }

            return success;

        } catch (Exception e) {
            log.error("插入文本并修改格式时发生异常", e);
            return false;
        }
    }

    /**
     * 在Word文档中插入合同编号（向后兼容方法）
     *
     * 这是一个便捷方法，专门用于在文档中插入合同编号。
     * 使用默认的合同编号模式匹配"合同编号："或"编号："，并设置为右对齐。
     *
     * 功能特点：
     * - 自动匹配"合同编号："或"编号："模式
     * - 设置为右对齐，符合合同文档规范
     * - 只插入第一个匹配项，避免重复
     * - 自动设置段落间距，避免换行问题
     *
     * 使用场景：
     * - 合同文档中插入合同编号
     * - 协议文档中插入协议编号
     * - 需要快速插入合同编号的场景
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param contractNum 要插入的合同编号
     * @return true 如果插入成功，false 如果插入失败
     *
     * @example
     * <pre>
     * WordTextEditService service = new WordTextEditService();
     * boolean success = service.addContractNumToDoc("/path/to/contract.docx", "HT-2025-001");
     * </pre>
     *
     * @deprecated 建议使用 insertTextAndFormatParagraph 方法以获得更多控制选项
     */
    public boolean addContractNumToDoc(String docFilePath, String contractNum) {
        return insertTextAndFormatParagraph(docFilePath, DEFAULT_CONTRACT_NUM_PATTERN, contractNum,
                                         InsertMode.FIRST_MATCH_ONLY, TextAlignment.RIGHT);
    }

    /**
     * 在Word文档中插入合同编号（增强版本）
     *
     * 这是 addContractNumToDoc 的增强版本，允许自定义插入模式和对齐方式。
     * 仍然使用默认的合同编号模式，但提供更多的控制选项。
     *
     * 功能特点：
     * - 自动匹配"合同编号："或"编号："模式
     * - 可自定义插入模式（首次匹配或全部匹配）
     * - 可自定义文本对齐方式（左对齐、居中、右对齐、两端对齐）
     * - 自动设置段落间距，避免换行问题
     *
     * 使用场景：
     * - 需要插入多个合同编号的文档
     * - 需要特定对齐方式的合同文档
     * - 批量处理合同文档
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param contractNum 要插入的合同编号
     * @param insertMode 插入模式：FIRST_MATCH_ONLY（只插入第一个）或ALL_MATCHES（插入所有匹配项）
     * @param textAlignment 文本对齐方式
     * @return true 如果插入成功，false 如果插入失败
     *
     * @example
     * <pre>
     * WordTextEditService service = new WordTextEditService();
     * // 插入所有匹配的合同编号，设置为居中对齐
     * boolean success = service.addContractNumToDoc("/path/to/contract.docx", "HT-2025-001",
     *     InsertMode.ALL_MATCHES, TextAlignment.CENTER);
     * </pre>
     *
     * @deprecated 建议使用 insertTextAndFormatParagraph 方法以获得更多控制选项
     */
    public boolean addContractNumToDoc(String docFilePath, String contractNum, InsertMode insertMode,
            TextAlignment textAlignment) {
        return insertTextAndFormatParagraph(docFilePath, DEFAULT_CONTRACT_NUM_PATTERN, contractNum,
        insertMode, textAlignment);
    }

    /**
     * 替换Word文档中指定的文本
     *
     * 此方法会在文档中查找指定的旧文本，并将其替换为新的文本内容。
     * 替换过程中保持原有文档格式不变，只更新文本内容。
     *
     * 功能特点：
     * - 支持段落和表格中的文本替换
     * - 保持原有文本格式（字体、颜色、大小等）
     * - 基于精确文本匹配进行替换
     * - 支持中英文混合文本
     *
     * 使用场景：
     * - 更新合同编号（如：HT-2024-001 → HT-2025-001）
     * - 更新项目代码（如：PRJ-2024-001 → PRJ-2025-001）
     * - 更新日期信息（如：2024年 → 2025年）
     * - 更新版本号或其他标识信息
     *
     * 注意事项：
     * - 替换基于精确文本匹配，区分大小写
     * - 如果文档中未找到要替换的文本，方法会返回false
     * - 支持批量替换多个文档
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param oldText 要替换的旧文本（必须与文档中的文本完全一致）
     * @param newText 新的文本内容
     * @return true 如果替换成功，false 如果替换失败或未找到要替换的文本
     *
     * @throws RuntimeException 如果文档读取或保存过程中发生异常
     *
     * @example
     * <pre>
     * WordTextEditService service = new WordTextEditService();
     * // 替换合同编号
     * boolean success = service.replaceText("/path/to/contract.docx", "HT-2024-001", "HT-2025-001");
     *
     * // 替换项目代码
     * boolean success = service.replaceText("/path/to/project.docx", "PRJ-2024-001", "PRJ-2025-001");
     * </pre>
     */
    public boolean replaceText(String docFilePath, String oldText, String newText) {
        if (docFilePath == null || docFilePath.trim().isEmpty()) {
            log.error("文档文件路径不能为空");
            return false;
        }

        if (oldText == null || oldText.trim().isEmpty()) {
            log.error("要替换的旧文本不能为空");
            return false;
        }

        if (newText == null || newText.trim().isEmpty()) {
            log.error("新的文本内容不能为空");
            return false;
        }

        try {
            log.info("开始替换文档中的文本：{} -> {}", oldText, newText);

            // 读取文档
            XWPFDocument document = readDocument(docFilePath);
            if (document == null) {
                return false;
            }

            // 处理文档内容
            boolean success = processDocumentForReplace(document, oldText, newText);
            if (!success) {
                log.warn("文档处理完成，但未找到要替换的文本");
                return false;
            }

            // 保存文档
            success = saveDocument(document, docFilePath);
            if (success) {
                log.info("文本替换成功，文档已保存");
            }

            return success;

        } catch (Exception e) {
            log.error("替换文本时发生异常", e);
            return false;
        }
    }

    /**
     * 替换Word文档中的合同编号
     *
     * 这是一个便捷方法，专门用于替换文档中的合同编号。
     * 内部调用 replaceText 方法，提供更语义化的API。
     *
     * 功能特点：
     * - 专门用于合同编号的替换操作
     * - 保持原有文档格式不变
     * - 支持段落和表格中的合同编号替换
     * - 基于精确文本匹配进行替换
     *
     * 使用场景：
     * - 合同编号版本更新
     * - 合同编号格式调整
     * - 批量更新多个文档的合同编号
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param oldContractNum 旧的合同编号（必须与文档中的文本完全一致）
     * @param newContractNum 新的合同编号
     * @return true 如果替换成功，false 如果替换失败或未找到要替换的合同编号
     *
     * @example
     * <pre>
     * WordTextEditService service = new WordTextEditService();
     * // 替换合同编号
     * boolean success = service.replaceContractNum("/path/to/contract.docx", "HT-2024-001", "HT-2025-001");
     *
     * // 批量替换多个文档
     * String[] docPaths = {"/path/to/contract1.docx", "/path/to/contract2.docx"};
     * for (String path : docPaths) {
     *     service.replaceContractNum(docPath, "HT-2024-001", "HT-2025-001");
     * }
     * </pre>
     */
    /**
     * 根据正则表达式模式替换Word文档中的文本
     *
     * 此方法使用正则表达式模式来查找和替换文档中的文本，比精确文本匹配更加灵活。
     * 支持复杂的匹配模式，如通配符、字符类、量词等。
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param pattern 用于匹配的正则表达式模式
     * @param replacement 替换文本，支持正则表达式替换模板
     * @return true 如果替换成功，false 如果替换失败或未找到匹配的文本
     */
    /**
     * 根据正则表达式模式替换Word文档中的文本（替换所有匹配项）
     *
     * 此方法使用正则表达式在Word文档中查找并替换所有匹配的文本。
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param pattern 用于匹配的正则表达式模式
     * @param replacement 替换文本，支持正则表达式替换模板
     * @return true 如果替换成功，false 如果替换失败或未找到匹配的文本
     */
    public boolean replaceTextByPattern(String docFilePath, Pattern pattern, String replacement) {
        return replaceTextByPattern(docFilePath, pattern, replacement, true);
    }

    /**
     * 根据正则表达式模式替换Word文档中的文本
     *
     * 此方法使用正则表达式在Word文档中查找并替换文本。支持复杂的匹配模式，
     * 包括捕获组、量词、字符类等正则表达式特性。
     *
     * 功能特点：
     * - 支持复杂的正则表达式模式
     * - 支持捕获组和替换模板
     * - 处理段落和表格中的内容
     * - 保持文档格式和样式
     * - 支持中英文混合内容
     * - 可控制替换数量（只替换第一个或全部替换）
     *
     * 使用场景：
     * - 批量替换合同编号
     * - 更新文档中的日期格式
     * - 替换邮箱地址或电话号码
     * - 批量修改产品代码
     *
     * 注意事项：
     * - 正则表达式区分大小写
     * - 替换会保持原有的文本格式
     * - 如果文档读取失败，方法会返回false
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param pattern 用于匹配的正则表达式模式
     * @param replacement 替换文本，支持正则表达式替换模板
     * @param replaceAll 是否替换所有匹配项，true表示替换全部，false表示只替换第一个
     * @return true 如果替换成功，false 如果替换失败或未找到匹配的文本
     */
    public boolean replaceTextByPattern(String docFilePath, Pattern pattern, String replacement, boolean replaceAll) {
        if (docFilePath == null || docFilePath.trim().isEmpty()) {
            log.error("文档文件路径不能为空");
            return false;
        }

        if (pattern == null) {
            log.error("正则表达式模式不能为空");
            return false;
        }

        if (replacement == null) {
            log.error("替换文本不能为空");
            return false;
        }

        try {
            log.info("开始根据正则表达式模式替换文档中的文本，模式：{}，替换为：{}，替换模式：{}",
                    pattern.pattern(), replacement, replaceAll ? "全部替换" : "只替换第一个");

            // 读取文档
            XWPFDocument document = readDocument(docFilePath);
            if (document == null) {
                return false;
            }

            // 处理文档内容
            boolean success = processDocumentForPatternReplace(document, pattern, replacement, replaceAll);
            if (!success) {
                log.warn("文档处理完成，但未找到匹配正则表达式的文本");
                return false;
            }

            // 保存文档
            success = saveDocument(document, docFilePath);
            if (success) {
                log.info("正则表达式文本替换成功，文档已保存");
            }

            return success;

        } catch (Exception e) {
            log.error("根据正则表达式替换文本时发生异常", e);
            return false;
        }
    }

    /**
     * 根据正则表达式模式替换Word文档中的文本（字符串版本，替换所有匹配项）
     *
     * 这是 replaceTextByPattern 的便捷版本，接受字符串形式的正则表达式。
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param patternString 正则表达式模式字符串
     * @param replacement 替换文本，支持正则表达式替换模板
     * @return true 如果替换成功，false 如果替换失败或未找到匹配的文本
     */
    public boolean replaceTextByPattern(String docFilePath, String patternString, String replacement) {
        return replaceTextByPattern(docFilePath, patternString, replacement, true);
    }

    /**
     * 根据正则表达式模式替换Word文档中的文本（字符串版本）
     *
     * 这是 replaceTextByPattern 的便捷版本，接受字符串形式的正则表达式。
     *
     * @param docFilePath 要处理的Word文档文件路径
     * @param patternString 正则表达式模式字符串
     * @param replacement 替换文本，支持正则表达式替换模板
     * @param replaceAll 是否替换所有匹配项，true表示替换全部，false表示只替换第一个
     * @return true 如果替换成功，false 如果替换失败或未找到匹配的文本
     */
    public boolean replaceTextByPattern(String docFilePath, String patternString, String replacement, boolean replaceAll) {
        try {
            Pattern pattern = Pattern.compile(patternString);
            return replaceTextByPattern(docFilePath, pattern, replacement, replaceAll);
        } catch (Exception e) {
            log.error("正则表达式模式编译失败：{}", patternString, e);
            return false;
        }
    }

    public boolean replaceContractNum(String docFilePath, String oldContractNum, String newContractNum) {
        return replaceText(docFilePath, oldContractNum, newContractNum);
    }

    /**
     * 检查Word文档中是否包含匹配指定模式的内容
     *
     * 此方法用于检查文档中是否存在匹配指定正则表达式模式的内容。
     * 可以用于在插入或替换文本之前，验证文档是否包含预期的关键词。
     *
     * 功能特点：
     * - 支持段落和表格中的内容检查
     * - 使用正则表达式进行模式匹配
     * - 提供快速的内容存在性验证
     * - 支持复杂的匹配模式
     *
     * 使用场景：
     * - 检查文档是否包含"合同编号："模式
     * - 验证文档结构是否符合预期
     * - 在插入文本前进行预检查
     * - 批量处理前的文档筛选
     *
     * 注意事项：
     * - 模式匹配区分大小写
     * - 支持中英文混合内容
     * - 如果文档读取失败，方法会返回false
     *
     * @param docFilePath 要检查的Word文档文件路径
     * @param keywordPattern 关键词匹配模式（正则表达式）
     * @return true 如果文档包含匹配的内容，false 如果不包含或检查失败
     *
     * @example
     * <pre>
     * WordTextEditService service = new WordTextEditService();
     *
     * // 检查是否包含合同编号模式
     * Pattern contractPattern = Pattern.compile("^(合同编号|编号)\\s*[:：]\\s*$");
     * boolean hasContractPattern = service.hasMatchingContent("/path/to/doc.docx", contractPattern);
     *
     * // 检查是否包含项目编号模式
     * Pattern projectPattern = Pattern.compile("^(项目编号)\\s*[:：]\\s*$");
     * boolean hasProjectPattern = service.hasMatchingContent("/path/to/doc.docx", projectPattern);
     * </pre>
     */
    public boolean hasMatchingContent(String docFilePath, Pattern keywordPattern) {
        if (docFilePath == null || keywordPattern == null) {
            return false;
        }

        try {
            XWPFDocument document = readDocument(docFilePath);
            if (document == null) {
                return false;
            }

            return hasMatchingContentInDocument(document, keywordPattern);

        } catch (Exception e) {
            log.error("检查文档内容时发生异常", e);
            return false;
        }
    }


    /**
     * 读取Word文档
     */
    private XWPFDocument readDocument(String docFilePath) {
        try (FileInputStream fis = new FileInputStream(docFilePath)) {
            XWPFDocument document = new XWPFDocument(fis);
            log.debug("文档读取成功：{}", docFilePath);
            return document;
        } catch (IOException e) {
            log.error("读取文档失败：{}", docFilePath, e);
            return null;
        }
    }

    /**
     * 检查文档中是否包含匹配的关键词模式
     */
    private boolean hasMatchingContentInDocument(XWPFDocument document, Pattern keywordPattern) {
        // 检查段落
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            if (keywordPattern.matcher(text).matches()) {
                log.debug("找到符合的段落：{}", text);
                return true;
            }
        }

        // 检查表格
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if (keywordPattern.matcher(paragraph.getText().trim()).matches()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * 保存文档
     */
    private boolean saveDocument(XWPFDocument document, String docFilePath) {
        try (FileOutputStream fos = new FileOutputStream(docFilePath)) {
            document.write(fos);
            document.close();
            log.debug("文档保存成功：{}", docFilePath);
            return true;
        } catch (IOException e) {
            log.error("保存文档失败：{}", docFilePath, e);
            return false;
        }
    }

    /**
     * 清理段落内容，移除前面的空格和制表符
     */
    private void cleanParagraphContent(XWPFParagraph paragraph) {
        // 移除段落中所有的运行块
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
    }

    /**
     * 从段落文本中提取关键词
     */
    private String extractKeyword(String paragraphText) {
        String trimmedText = paragraphText.trim();

        // 提取关键词，移除前后的空格和制表符
        if (trimmedText.startsWith("合同编号")) {
            return "合同编号：";
        } else if (trimmedText.startsWith("编号")) {
            return "编号：";
        } else {
            // 如果无法识别，返回原文本
            return trimmedText;
        }
    }

    /**
     * 显示Word文档的详细内容（用于调试和分析）
     *
     * 此方法用于调试和分析Word文档的内容结构，输出文档中的所有段落和表格信息。
     * 在开发、测试和问题排查时非常有用。
     *
     * 功能特点：
     * - 显示文档中所有段落的内容
     * - 显示表格的行列信息
     * - 过滤空段落，只显示有内容的段落
     * - 提供结构化的日志输出
     *
     * 使用场景：
     * - 调试文本插入和替换功能
     * - 分析文档结构和内容
     * - 验证文档处理结果
     * - 问题排查和诊断
     *
     * 注意事项：
     * - 会读取整个文档到内存中
     * - 输出大量日志信息，建议在调试时使用
     * - 如果文档很大，可能影响性能
     *
     * @param docPath 要分析的Word文档文件路径
     * @param stage 当前处理阶段标识（如："原始状态"、"插入后"、"替换后"）
     * @throws IOException 如果文档读取失败
     *
     * @example
     * <pre>
     * WordTextEditService service = new WordTextEditService();
     *
     * // 显示文档原始内容
     * service.displayDocumentContent("/path/to/doc.docx", "原始状态");
     *
     * // 显示处理后的内容
     * service.displayDocumentContent("/path/to/doc.docx", "插入后");
     * </pre>
     */
    public void displayDocumentContent(String docPath, String stage) throws IOException {
        try (FileInputStream fis = new FileInputStream(docPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            log.info("=== {} 文档内容 ===", stage);

            // 显示段落内容
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            log.info("文档包含 {} 个段落:", paragraphs.size());

            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);
                String text = paragraph.getText();
                if (!text.trim().isEmpty()) {
                    log.info("段落 {}: '{}'", i + 1, text);
                }
            }

            // 显示表格内容
            List<XWPFTable> tables = document.getTables();
            if (!tables.isEmpty()) {
                log.info("文档包含 {} 个表格:", tables.size());
                for (int i = 0; i < tables.size(); i++) {
                    XWPFTable table = tables.get(i);
                    log.info("表格 {}: {} 行 x {} 列", i + 1,
                            table.getRows().size(),
                            table.getRows().isEmpty() ? 0 : table.getRow(0).getTableCells().size());
                }
            }

            log.info("=== {} 文档内容结束 ===", stage);
        }
    }

    /**
     * 处理文档内容，只插入文本不修改格式
     */
    private boolean processDocumentForInsert(XWPFDocument document, Pattern keywordPattern, String insertText,
                                           InsertMode insertMode) {
        boolean found = false;
        int insertCount = 0;

        // 处理段落
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (processParagraphForInsert(paragraph, keywordPattern, insertText)) {
                found = true;
                insertCount++;

                if (insertMode == InsertMode.FIRST_MATCH_ONLY) {
                    log.info("找到第一个匹配项，插入完成");
                    break;
                }
            }
        }

        // 如果只插入第一个匹配项且已经找到，则跳过表格处理
        if (insertMode == InsertMode.FIRST_MATCH_ONLY && found) {
            log.info("段落中找到匹配项，跳过表格处理");
        } else {
            // 处理表格
            for (XWPFTable table : document.getTables()) {
                if (processTableForInsert(table, keywordPattern, insertText)) {
                    found = true;
                    insertCount++;

                    if (insertMode == InsertMode.FIRST_MATCH_ONLY) {
                        log.info("表格中找到匹配项，插入完成");
                        break;
                    }
                }
            }
        }

        if (found) {
            log.info("文档处理完成，共插入 {} 处文本", insertCount);
        }

        return found;
    }

    /**
     * 处理段落，只插入文本不修改格式
     */
    private boolean processParagraphForInsert(XWPFParagraph paragraph, Pattern keywordPattern, String insertText) {
        String paragraphText = paragraph.getText();

        // 检查段落是否匹配关键词模式
        if (keywordPattern.matcher(paragraphText.trim()).matches()) {
            log.debug("找到匹配的段落：{}", paragraphText);

            // 保留原有格式，只在末尾添加文本
            XWPFRun run = paragraph.createRun();
            run.setText(insertText);

            log.info("在段落中插入文本：{}", insertText);
            return true;
        }

        return false;
    }

    /**
     * 处理表格，只插入文本不修改格式
     */
    private boolean processTableForInsert(XWPFTable table, Pattern keywordPattern, String insertText) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                // 处理单元格中的段落
                List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                for (XWPFParagraph paragraph : cellParagraphs) {
                    if (processParagraphForInsert(paragraph, keywordPattern, insertText)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 处理文档内容，插入文本并修改段落格式
     */
    private boolean processDocumentWithFormat(XWPFDocument document, Pattern keywordPattern, String insertText,
                                            InsertMode insertMode, TextAlignment textAlignment) {
        boolean found = false;
        int insertCount = 0;

        // 处理段落
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (processParagraphWithFormat(paragraph, keywordPattern, insertText, textAlignment)) {
                found = true;
                insertCount++;

                if (insertMode == InsertMode.FIRST_MATCH_ONLY) {
                    log.info("找到第一个匹配项，插入完成");
                    break;
                }
            }
        }

        // 如果只插入第一个匹配项且已经找到，则跳过表格处理
        if (insertMode == InsertMode.FIRST_MATCH_ONLY && found) {
            log.info("段落中找到匹配项，跳过表格处理");
        } else {
            // 处理表格
            for (XWPFTable table : document.getTables()) {
                if (processTableWithFormat(table, keywordPattern, insertText, textAlignment)) {
                    found = true;
                    insertCount++;

                    if (insertMode == InsertMode.FIRST_MATCH_ONLY) {
                        log.info("表格中找到匹配项，插入完成");
                        break;
                    }
                }
            }
        }

        if (found) {
            log.info("文档处理完成，共插入并格式化 {} 处文本", insertCount);
        }

        return found;
    }

    /**
     * 处理段落，插入文本并修改段落格式
     */
    private boolean processParagraphWithFormat(XWPFParagraph paragraph, Pattern keywordPattern, String insertText,
                                             TextAlignment textAlignment) {
        String paragraphText = paragraph.getText();

        // 检查段落是否匹配关键词模式
        if (keywordPattern.matcher(paragraphText.trim()).matches()) {
            log.debug("找到匹配的段落：{}", paragraphText);

            // 清理段落内容，移除前面的空格和制表符
            cleanParagraphContent(paragraph);

            // 重新创建内容：关键词 + 插入文本
            String keyword = extractKeyword(paragraphText);
            log.debug("提取的关键词: '{}'", keyword);

            // 创建新的运行块
            XWPFRun run = paragraph.createRun();
            String finalText = keyword + insertText;
            run.setText(finalText);
            log.debug("插入的最终文本: '{}'", finalText);

            // 设置段落属性
            paragraph.setSpacingBefore(0);
            paragraph.setSpacingAfter(0);
            paragraph.setAlignment(textAlignment.getAlignment());

            log.info("在段落中插入文本并修改格式：{}", insertText);
            return true;
        }

        return false;
    }

    /**
     * 处理表格，插入文本并修改段落格式
     */
    private boolean processTableWithFormat(XWPFTable table, Pattern keywordPattern, String insertText,
                                         TextAlignment textAlignment) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                // 处理单元格中的段落
                List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                for (XWPFParagraph paragraph : cellParagraphs) {
                    if (processParagraphWithFormat(paragraph, keywordPattern, insertText, textAlignment)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 处理文档内容，替换指定的文本
     */
    private boolean processDocumentForReplace(XWPFDocument document, String oldText, String newText) {
        boolean found = false;
        int replaceCount = 0;

        // 处理段落
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (processParagraphForReplace(paragraph, oldText, newText)) {
                found = true;
                replaceCount++;
            }
        }

        // 处理表格
        for (XWPFTable table : document.getTables()) {
            if (processTableForReplace(table, oldText, newText)) {
                found = true;
                replaceCount++;
            }
        }

        if (found) {
            log.info("文档处理完成，共替换 {} 处文本", replaceCount);
        }

        return found;
    }

    /**
     * 处理文档，根据正则表达式模式替换文本
     */
    private boolean processDocumentForPatternReplace(XWPFDocument document, Pattern pattern, String replacement, boolean replaceAll) {
        boolean found = false;
        int replaceCount = 0;

        // 处理段落
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (processParagraphForPatternReplace(paragraph, pattern, replacement, replaceAll)) {
                found = true;
                replaceCount++;
                // 如果只替换第一个，找到后就可以停止
                if (!replaceAll) {
                    log.info("找到第一个匹配项并替换完成，停止处理");
                    break;
                }
            }
        }

        // 如果只替换第一个且已经在段落中找到，则不再处理表格
        if (!replaceAll && found) {
            log.info("文档处理完成，替换第一个匹配项，共替换 {} 处文本", replaceCount);
            return found;
        }

        // 处理表格
        for (XWPFTable table : document.getTables()) {
            if (processTableForPatternReplace(table, pattern, replacement, replaceAll)) {
                found = true;
                replaceCount++;
                // 如果只替换第一个，找到后就可以停止
                if (!replaceAll) {
                    log.info("在表格中找到第一个匹配项并替换完成，停止处理");
                    break;
                }
            }
        }

        if (found) {
            log.info("文档处理完成，共替换 {} 处文本", replaceCount);
        }

        return found;
    }

    /**
     * 处理段落，根据正则表达式模式替换文本
     */
    private boolean processParagraphForPatternReplace(XWPFParagraph paragraph, Pattern pattern, String replacement, boolean replaceAll) {
        String paragraphText = paragraph.getText();

        // 检查段落是否包含匹配正则表达式的文本
        if (pattern.matcher(paragraphText).find()) {
            log.debug("找到匹配正则表达式的段落：{}", paragraphText);

            // 在段落级别进行正则表达式替换，避免运行块分割问题
            try {
                // 根据replaceAll参数决定替换方式
                String newParagraphText;
                if (replaceAll) {
                    // 替换所有匹配项
                    newParagraphText = pattern.matcher(paragraphText).replaceAll(replacement);
                } else {
                    // 只替换第一个匹配项
                    newParagraphText = pattern.matcher(paragraphText).replaceFirst(replacement);
                }

                // 清空段落中的所有运行块
                List<XWPFRun> runs = paragraph.getRuns();
                for (int i = runs.size() - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }

                // 创建新的运行块，包含替换后的完整段落文本
                XWPFRun newRun = paragraph.createRun();
                newRun.setText(newParagraphText);

                log.info("在段落中根据正则表达式替换文本，模式：{}，替换为：{}，替换模式：{}",
                    pattern.pattern(), replacement, replaceAll ? "全部替换" : "只替换第一个");
                return true;
            } catch (Exception e) {
                log.warn("段落级正则表达式替换失败，尝试逐块替换方式", e);

                // 备用方案：逐块替换（原来的逻辑）
                return processParagraphRunsForPatternReplace(paragraph, pattern, replacement, replaceAll);
            }
        }

        return false;
    }

    /**
     * 逐块替换的备用方案（用于正则表达式替换）
     */
    private boolean processParagraphRunsForPatternReplace(XWPFParagraph paragraph, Pattern pattern, String replacement, boolean replaceAll) {
        List<XWPFRun> runs = paragraph.getRuns();
        boolean replaced = false;

        // 遍历所有运行块，查找并替换文本
        for (XWPFRun run : runs) {
            String runText = run.getText(0);
            if (runText != null && pattern.matcher(runText).find()) {
                // 根据replaceAll参数决定替换方式
                String newRunText;
                if (replaceAll) {
                    // 替换所有匹配项
                    newRunText = pattern.matcher(runText).replaceAll(replacement);
                } else {
                    // 只替换第一个匹配项
                    newRunText = pattern.matcher(runText).replaceFirst(replacement);
                }
                run.setText(newRunText, 0);
                replaced = true;
                log.debug("在运行块中替换文本：{} -> {}", runText, newRunText);

                // 如果只替换第一个，找到后就可以停止
                if (!replaceAll) {
                    break;
                }
            }
        }

        if (replaced) {
            log.info("在段落运行块中根据正则表达式替换文本，模式：{}，替换为：{}，替换模式：{}",
                pattern.pattern(), replacement, replaceAll ? "全部替换" : "只替换第一个");
            return true;
        }

        return false;
    }

    /**
     * 处理表格，根据正则表达式模式替换文本
     */
    private boolean processTableForPatternReplace(XWPFTable table, Pattern pattern, String replacement, boolean replaceAll) {
        boolean found = false;

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                // 处理单元格中的段落
                List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                for (XWPFParagraph paragraph : cellParagraphs) {
                    if (processParagraphForPatternReplace(paragraph, pattern, replacement, replaceAll)) {
                        found = true;
                        // 如果只替换第一个，找到后就可以停止
                        if (!replaceAll) {
                            log.info("在表格中找到第一个匹配项并替换完成，停止处理");
                            return found;
                        }
                    }
                }
            }
        }

        return found;
    }

    /**
     * 处理段落，替换指定的文本
     */
    private boolean processParagraphForReplace(XWPFParagraph paragraph, String oldText, String newText) {
        String paragraphText = paragraph.getText();

        // 检查段落是否包含要替换的文本
        if (paragraphText.contains(oldText)) {
            log.debug("找到包含要替换文本的段落：{}", paragraphText);

            // 方案1：智能替换 - 保持原有格式，只替换匹配的部分
            try {
                // 在段落文本中进行替换
                String newParagraphText = paragraphText.replace(oldText, newText);

                // 清空段落中的所有运行块
                List<XWPFRun> runs = paragraph.getRuns();
                for (int i = runs.size() - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }

                // 创建新的运行块，包含替换后的完整段落文本
                XWPFRun newRun = paragraph.createRun();
                newRun.setText(newParagraphText);

                log.info("在段落中智能替换文本：{} -> {}", oldText, newText);
                return true;
            } catch (Exception e) {
                log.warn("智能替换失败，尝试逐块替换方式", e);

                // 方案2：回退到逐块替换（原来的逻辑）
                return processParagraphRunsForReplace(paragraph, oldText, newText);
            }
        }

        return false;
    }

    /**
     * 逐块替换的备用方案
     */
    private boolean processParagraphRunsForReplace(XWPFParagraph paragraph, String oldText, String newText) {
        List<XWPFRun> runs = paragraph.getRuns();
        boolean replaced = false;

        // 遍历所有运行块，查找并替换文本
        for (XWPFRun run : runs) {
            String runText = run.getText(0);
            if (runText != null && runText.contains(oldText)) {
                // 替换文本
                String newRunText = runText.replace(oldText, newText);
                run.setText(newRunText, 0);
                replaced = true;
                log.debug("在运行块中替换文本：{} -> {}", oldText, newText);
            }
        }

        if (replaced) {
            log.info("在段落运行块中替换文本：{} -> {}", oldText, newText);
            return true;
        }

        return false;
    }

    /**
     * 处理表格，替换指定的文本
     */
    private boolean processTableForReplace(XWPFTable table, String oldText, String newText) {
        boolean found = false;

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                // 处理单元格中的段落
                List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                for (XWPFParagraph paragraph : cellParagraphs) {
                    if (processParagraphForReplace(paragraph, oldText, newText)) {
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    // Getter 和 Setter 方法
    public Pattern getKeywordPattern() {
        return keywordPattern;
    }

    public void setKeywordPattern(Pattern keywordPattern) {
        this.keywordPattern = keywordPattern;
    }

    public InsertMode getInsertMode() {
        return insertMode;
    }

    public void setInsertMode(InsertMode insertMode) {
        this.insertMode = insertMode;
    }

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
    }

    public boolean isRemoveLeadingSpaces() {
        return removeLeadingSpaces;
    }

    public void setRemoveLeadingSpaces(boolean removeLeadingSpaces) {
        this.removeLeadingSpaces = removeLeadingSpaces;
    }

    public boolean isRemoveTrailingSpaces() {
        return removeTrailingSpaces;
    }

    public void setRemoveTrailingSpaces(boolean removeTrailingSpaces) {
        this.removeTrailingSpaces = removeTrailingSpaces;
    }
}
