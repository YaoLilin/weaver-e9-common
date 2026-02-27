package com.customization.yll.common.doc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.io.File;

/**
 * @author 姚礼林
 * @desc wps 文档转换接口测试
 * @date 2025/6/18
 **/
public class DocConvertorByWpsApiTest {


    private static final String OA_HOST = "http://34f7fd1d.r10.cpolar.top";
    private static final String SECRET_KEY = "SKantbctxslusqeh";
    private static final String ACCESS_KEY = "VOUEGNLHBAGHVACW";

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void convert() {
        DocConvertorByWpsApi convertor = new DocConvertorByWpsApi(OA_HOST,
                "https://htxm.lcvc.edu.cn", SECRET_KEY, ACCESS_KEY);
        File file = convertor.convert(12877, GCONST.getRootPath() + "/filesystem/temp/test.docx",
                "docx", null);
        Assert.assertNotNull( file);
    }
}
