package com.customization.yll.common.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaolilin
 * @desc DbUtil测试类
 * @date 2024/12/24
 **/
public class DbUtilTest {

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void batchUpdate() {
        List<Map<String, Object>> updateDatas = new ArrayList<>();
        List<Map<String, Object>> conditons = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("mc", "111");
        item1.put("zd1", "111");
        updateDatas.add(item1);
        Map<String, Object> condItem1 = new HashMap<>();
        condItem1.put("id", 36);
        conditons.add(condItem1);
        Map<String, Object> item2 = new HashMap<>();
        item2.put("mc", "222");
        item2.put("zd1", "222");
        updateDatas.add(item2);
        Map<String, Object> condItem2 = new HashMap<>();
        condItem2.put("id", 35);
        conditons.add(condItem2);
        boolean result = DbUtil.batchUpdate(updateDatas, conditons, "uf_test_mode");
        Assert.assertTrue(result);
    }

}
