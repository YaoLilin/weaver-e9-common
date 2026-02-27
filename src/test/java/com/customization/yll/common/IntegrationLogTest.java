package com.customization.yll.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author 姚礼林
 * @desc IntegrationLogTest
 * @date 2025/8/6
 **/
public class IntegrationLogTest {

    @Test
    public void info1() {
        IntegrationLog log = new IntegrationLog(this.getClass());
        log.info("hello {},{}", 1, 2);
        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {
            log.error("hello {}",1,e);
        }
    }
}
