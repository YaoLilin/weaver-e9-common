package com.customization.yll.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetTrans;
import weaver.general.GCONST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author 姚礼林
 * @desc ModeUtil 测试类
 * @date 2025/9/2
 **/
class ModeUtilTest {

    @BeforeEach
    void setUp() {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    void batchInsertToMode() {
        Map<String ,Object> data1 = new HashMap<>(5);
        data1.put("mc", "张三");
        data1.put("zd1", "111");
        Map<String ,Object> data2 = new HashMap<>(5);
        data2.put("mc", "张四");
        data2.put("zd1", "222");
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(data1);
        data.add(data2);
        RecordSet recordSet = new RecordSet();
        int modeId = ModeUtil.getModeIdByTableName("uf_test_mode");
        boolean result = ModeUtil.batchInsertToMode(data, "uf_test_mode", modeId,
                true, recordSet);
        assertTrue(result);
    }

    @Test
    void batchInsertToModeByRsInterface_withTrans() {
        Map<String, Object> data1 = new HashMap<>(5);
        data1.put("mc", "张三");
        data1.put("zd1", "111");
        Map<String, Object> data2 = new HashMap<>(5);
        data2.put("mc", "张四");
        data2.put("zd1", "222");
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(data1);
        data.add(data2);
        RecordSetTrans recordSet = new RecordSetTrans();
        recordSet.setAutoCommit(false);
        int modeId = ModeUtil.getModeIdByTableName("uf_test_mode");
        boolean result = false;
        try {
            result = ModeUtil.batchInsertToModeByRsInterface(data, "uf_test_mode", modeId,
                    true, recordSet);
            recordSet.commit();
        } catch (Exception e) {
            e.printStackTrace();
            recordSet.rollback();
        }
        assertTrue(result);
    }

    @Test
    void insertToMode() {
        Map<String, Object> data1 = new HashMap<>(5);
        data1.put("mc", "张三");
        data1.put("zd1", "111");
        RecordSet recordSet = new RecordSet();
        boolean result = ModeUtil.insertToMode(data1, "uf_test_mode", recordSet);
        assertTrue(result);
    }
}
