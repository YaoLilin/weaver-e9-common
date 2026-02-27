package com.customization.yll.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weaver.conn.RecordSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author 姚礼林
 * @desc 人力资源信息获取工具类测试
 * @date 2025/10/30
 **/
class HrmInfoUtilTest {

    @BeforeEach
    void setUp() {
        TestUtil.setLocalServer();
    }

    @Test
    void getFullDepartmentPath() {
        String fullDepartmentPath = HrmInfoUtil.getFullDepartmentPath(17, "/", new RecordSet());
        System.out.println(fullDepartmentPath);
        assertEquals("EBU技术中心/工作流程组", fullDepartmentPath);
    }
}
