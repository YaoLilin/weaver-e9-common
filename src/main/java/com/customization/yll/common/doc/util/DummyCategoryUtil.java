//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.customization.yll.common.doc.util;

import weaver.conn.RecordSet;
import weaver.docs.category.DocTreeDocFieldComInfo;
import weaver.general.TimeUtil;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

/**
 * 虚拟目录工具类
 * @author yaolilin
 */
public class DummyCategoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(DummyCategoryUtil.class);


    private DummyCategoryUtil() {
    }


    /**
     * 将文档移动到指定虚拟目录
     * @param docId 文档id
     * @param categoryId 虚拟目录id
     * @param recordSet recordSet
     * @return 是否移动成功
     */
    public static boolean putDocToDummyCategory(int docId, int categoryId, RecordSet recordSet) {
        try {
            String currentDate = TimeUtil.getCurrentDateString();
            String onlyCurrentTime = TimeUtil.getOnlyCurrentTimeString();
            DocTreeDocFieldComInfo docTreeDocFieldComInfo = new DocTreeDocFieldComInfo();
            if (!docTreeDocFieldComInfo.isHaveSameOne(categoryId+"", docId+"")) {
                String sql = "insert into DocDummyDetail(catelogid,docid,importdate,importtime) " +
                        "values (" + categoryId + "," + docId + ",'" + currentDate + "','" + onlyCurrentTime + "')";
                if (!recordSet.executeUpdate(sql)) {
                    logger.error("移动到虚拟目录失败,执行sql失败");
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error("移动到虚拟目录失败" , e);
            return false;
        }
        return true;
    }


}
