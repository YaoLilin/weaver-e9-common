package com.customization.yll.common.util;

import com.customization.yll.common.enu.LanguageType;
import lombok.experimental.UtilityClass;
import weaver.conn.RecordSet;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 姚礼林
 * @desc 多语言工具
 * @date 2024/7/5
 */
@UtilityClass
public class MultiLanguageUtil {
    /**
     * 转换为多语言格式文本
     * @param cnName 中文名称
     * @param enName 英文名称
     * @param recordSet recordSet
     * @return 多语言格式文本
     */
    public static String getMultiLanguage(String cnName, String enName, RecordSet recordSet) {
        if (cnName == null || cnName.isEmpty()) {
            return "";
        }
        if (enName == null || enName.isEmpty()) {
            return cnName;
        }
        Map<Integer, String> languageInfo = getLanguageInfo(recordSet);
        int chineseId =  0;
        int englishId = 0;
        for (Map.Entry<Integer, String> entry : languageInfo.entrySet()) {
            if ("简体中文".equals(entry.getValue())) {
                chineseId = entry.getKey();
            }
            if ("English".equals(entry.getValue())) {
                englishId =  entry.getKey();
            }
        }
        return "~`~" + String.format("`%d %s`~", chineseId, cnName) +
                String.format("`%d %s`~", englishId, enName) +
                "`~";
    }

    /**
     * 获取系统里的多语言信息
     * @param recordSet recordSet
     * @return map，key为语言id，value为语言名称
     */
    public static Map<Integer, String> getLanguageInfo(RecordSet recordSet) {
        Map<Integer, String> languageInfo = new HashMap<>(10);
        recordSet.executeQuery("SELECT id,language from syslanguage");
        while (recordSet.next()) {
            languageInfo.put(recordSet.getInt("id"), recordSet.getString("language"));
        }
        return languageInfo;
    }

    /**
     * 解析多语言文本，获取文本中的指定语言文本，如人力资源姓名的多语言文本，获取中文名称
     * @param multiLanguageText 多语言格式文本
     * @param languageType 指定语言
     * @param recordSet recordSet
     * @return 多语言文本中的指定语言文本
     */
    public static String analyzeMultiLanguageText(String multiLanguageText,LanguageType languageType,RecordSet recordSet) {
        if (!multiLanguageText.contains("~`")) {
            return multiLanguageText;
        }
        int languageId = 0;
        Map<Integer, String> languageInfo = getLanguageInfo(recordSet);
        for (Map.Entry<Integer, String> entry : languageInfo.entrySet()) {
            if (entry.getValue().equals(languageType.getName())) {
                languageId = entry.getKey();
            }
        }
        Pattern pattern = Pattern.compile("~`"+languageId+" (.*?)`");
        Matcher matcher = pattern.matcher(multiLanguageText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return multiLanguageText;
    }
}
