package com.customization.yll.common.manager;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import static org.junit.Assert.*;

/**
 * @author yaolilin
 * @desc todo
 * @date 2024/9/3
 **/
public class UploadFileManagerTest {
    private static final String  APP_ID ="552BD156-96EB-DE32-5D55-24F889A2FCAF";
    private static final String ADDRESS = "http://localhost";
    private static final String FILE_PATH = "/Users/yaolilin/Desktop/截屏2024-09-02 11.05.19.png";

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void uploadFile() {
        UploadFileManager fileManager = new UploadFileManager(ADDRESS, new EcologyTokenManager(APP_ID, ADDRESS));
        String result = fileManager.uploadFile(FILE_PATH);
        System.out.println("result :"+result);
        Assert.assertFalse(result.isEmpty());
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        Assert.assertTrue(data.containsKey("fileid"));
    }

    @Test
    public void uploadFile_withUser() {
        UploadFileManager fileManager = new UploadFileManager(ADDRESS,"30", new EcologyTokenManager(APP_ID, ADDRESS));
        String result = fileManager.uploadFile(FILE_PATH);
        System.out.println("result :"+result);
        Assert.assertFalse(result.isEmpty());
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        Assert.assertTrue(data.containsKey("fileid"));
    }
}
