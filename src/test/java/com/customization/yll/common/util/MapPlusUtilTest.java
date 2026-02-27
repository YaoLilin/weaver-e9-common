package com.customization.yll.common.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author 姚礼林
 * @desc MapPlusUtil 测试
 * @date 2025/7/11
 **/
public class MapPlusUtilTest {

    @Test(expected = RuntimeException.class)
    public void getOrElseThrow() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "1");
        MapPlusUtil.getOrElseThrow(map, "age", Integer.class, () ->
             new RuntimeException("age null")
        );
    }

    @Test(expected = RuntimeException.class)
    public void getOrElseThrow_withTypeError() {
        Map<String, Object> map = new HashMap<>();
        map.put("list", map);
        List value = MapPlusUtil.getOrElseThrow(map, "list", List.class, () ->
                new RuntimeException("list null")
        );
        System.out.println(value);
    }
}
