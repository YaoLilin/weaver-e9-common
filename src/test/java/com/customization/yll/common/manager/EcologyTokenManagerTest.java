package com.customization.yll.common.manager;

import com.cloudstore.dev.api.util.HttpManager;
import com.customization.yll.common.util.CacheUtil;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author yaolilin
 * @desc token 获取测试
 * @date 2024/9/3
 **/
public class EcologyTokenManagerTest {
        private static final String  APP_ID ="552BD156-96EB-DE32-5D55-24F889A2FCAF";
        private static final String ADDRESS = "localhost";


    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    private static void cleanCache() {
        CacheUtil.deleteCache("DEV_SERVER_TOKEN");
        CacheUtil.deleteCache("DEV_SERVER_SECRET");
        CacheUtil.deleteCache("DEV_SERVER_PUBLIC_KEY");
    }

    @Test
    public void getHeaderWithToken() {
        cleanCache();
        EcologyTokenManager ecologyTokenManager = new EcologyTokenManager(APP_ID, ADDRESS);
        Map<String, String> header = ecologyTokenManager.getHeaderWithToken("1");
        assertFalse(header.isEmpty());
        System.out.println(header);
    }

    @Test
    public void getToken() {
        cleanCache();
        EcologyTokenManager ecologyTokenManager = new EcologyTokenManager(APP_ID, ADDRESS);
        String token = ecologyTokenManager.getToken();
        System.out.println(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testGetTokenWithCache() {
        EcologyTokenManager ecologyTokenManager = new EcologyTokenManager(APP_ID, ADDRESS);
        String token = ecologyTokenManager.getToken();
        System.out.println("token : "+token);
        String  devServerSecret = (String) CacheUtil.getCache("DEV_SERVER_SECRET");
        assertFalse(devServerSecret.isEmpty());
        Map<String, String> headerWithToken = ecologyTokenManager.getHeaderWithToken("1");
        System.out.println(headerWithToken);
        assertFalse(headerWithToken.isEmpty());
    }

    @Test
    public void testCallApiWithToken() {
        cleanCache();
        HttpManager httpManager = new HttpManager();
        EcologyTokenManager ecologyTokenManager = new EcologyTokenManager(APP_ID, ADDRESS);
        Map<String, String> header = ecologyTokenManager.getHeaderWithToken("1");
        System.out.println("header："+header);
        String result = httpManager.getData("http://localhost/api/msgcenter/homepage/getMsgCount", new HashMap<>()
        ,header);
        System.out.println(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("count"));
    }
}
