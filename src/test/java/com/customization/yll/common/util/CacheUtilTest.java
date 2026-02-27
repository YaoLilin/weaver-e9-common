package com.customization.yll.common.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolilin
 * @desc 缓存工具类测试
 * @date 2025/1/14
 **/
public class CacheUtilTest {

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void putCache() {
        CacheUtil.putCache("testKey", "test", 60);
        Assert.assertEquals("test", CacheUtil.getCache("testKey"));

        CacheUtil.putCache("aa", "test");
        Assert.assertEquals("test", CacheUtil.getCache("aa"));
    }

    @Test
    public void putCache_withObject() {
        Map<String, Object> value = new HashMap<>(1);
        value.put("test", "1");
        CacheUtil.putCache("testKey", value, 60);
        Assert.assertNotNull(CacheUtil.getCache("testKey"));
        Map<String, Object> cacheValue = (Map<String, Object>) CacheUtil.getCache("testKey");
        System.out.println("cacheValue:" + cacheValue);
        Assert.assertEquals("1", cacheValue.get("test"));
    }

    @Test
    public void testGetCache() {
        CacheUtil.getCache("aa");
        Assert.assertEquals("1", CacheUtil.getCache("aa"));
    }

    @Test
    public void testExpire() throws InterruptedException {
        CacheUtil.putCache("testKey", "test", 10);
        System.out.println("value:"+CacheUtil.getCache("testKey"));
        Assert.assertNotNull(CacheUtil.getCache("testKey"));
        Thread.sleep(11000);
        Object value = CacheUtil.getCache("testKey");
        Assert.assertNull(value);
    }

}
