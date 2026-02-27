package com.customization.yll.common;

import lombok.experimental.UtilityClass;
import weaver.conn.RecordSet;

/**
 * @author 姚礼林
 * @desc RecordSet 工厂类
 * @date 2026/1/8
 **/
@UtilityClass
public class RecordSetFactory {

    /**
     * 创建一个 RecordSet 实例
     */
    public static RecordSet instance() {
        return new RecordSet();
    }
}
