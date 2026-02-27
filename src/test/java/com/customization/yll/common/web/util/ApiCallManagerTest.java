package com.customization.yll.common.web.util;

import com.customization.yll.common.manager.EcologyTokenManager;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolilin
 * @desc todo
 * @date 2024/12/4
 **/
public class ApiCallManagerTest {
    private ApiCallManager callManager;
    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
        callManager = new ApiCallManager();
    }

    @Test
    public void post() {
        EcologyTokenManager ecologyTokenManager =
                new EcologyTokenManager("552BD156-96EB-DE32-5D55-24F889A2FCAF","localhost");
        Map<String, String> header = new HashMap<>();
        header.put("testParam", "test");
        header.putAll(ecologyTokenManager.getHeaderWithToken("1"));
        String result = callManager.postResult("http://localhost/api/secondev/demo/test-api",
                "{\"name\":\"yaolilin 姚\"}", header);
        System.out.println(result);
    }

    @Test
    public void toFormParam() {
        Map<String, String> params = new HashMap<>(2);
        params.put("name", "yao");
        params.put("age", "18");

        String formParam = ApiCallManager.toFormParam(params);
        System.out.println(formParam);
    }
}
