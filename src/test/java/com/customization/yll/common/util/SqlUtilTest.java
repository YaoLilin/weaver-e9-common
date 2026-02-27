package com.customization.yll.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc sql 工具类测试
 * @date 2025/4/23
 **/
public class SqlUtilTest {

    @Test
    public void buildEqualsWhere() {
        Map<String, Object> conditions = new HashMap<>(2);
        conditions.put("name", "zhangsan");
        conditions.put("age", 18);
        List<Object> paramValues = new ArrayList<>();
        String whereSql = SqlUtil.buildEqualsWhere(conditions, paramValues);
        System.out.println(whereSql);
        Assert.assertEquals(" WHERE name = ? and age = ?", whereSql);
        Assert.assertEquals(2, paramValues.size());
    }
}
