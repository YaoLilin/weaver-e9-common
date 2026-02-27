package com.customization.yll.common.util;

import com.customization.yll.common.IntegrationLog;
import org.junit.Test;

/**
 * @author 姚礼林
 * @desc LogUtilTest
 * @date 2025/8/6
 **/
public class LogUtilTest {

    @Test
    public void getIntegrationLog() {
        IntegrationLog log = LogUtil.getIntegrationLog(this.getClass());
        log.info("success");
        log.info("info,{},{}", 1, 2);
        log.debug("info,{},{}", 1, 2);
        log.error("info,{},{}", 1, 2);
        log.warn("info,{},{}", 1, 2);
    }
}
