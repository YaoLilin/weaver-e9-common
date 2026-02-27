package com.customization.yll.common.util;

import lombok.experimental.UtilityClass;
import weaver.general.GCONST;

/**
 * @author 姚礼林
 * @desc 测试工具
 * @date 2025/7/24
 **/
@UtilityClass
public class TestUtil {

    public static void setLocalServer() {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }
}
