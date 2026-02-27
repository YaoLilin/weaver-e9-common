package com.customization.yll.common.mode.util;

import weaver.formmode.customjavacode.AbstractModeExpandJavaCodeNew;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModeActionUtil {
    /**
     * 生成 action 错误信息
     *
     * @param msg 错误信息
     * @return action 返回数据
     */
    public static Map<String, String> failed(String msg, Class<? extends AbstractModeExpandJavaCodeNew> c) {
        String errorMsg = msg == null ? String.format("[%s] action 执行失败", c.getSimpleName()) :
                String.format("[%s] action 执行失败：%s", c.getSimpleName(), msg);
        Map<String, String> result = new HashMap<>(2);
        result.put("errmsg", errorMsg);
        result.put("flag", "false");
        return result;
    }

    /**
     * 生成 action 错误信息
     * @return action 返回数据
     */
    public static Map<String, String> failed(Class<? extends AbstractModeExpandJavaCodeNew> c) {
        return failed(null, c);
    }

    public static Map<String, String> success() {
        return Collections.emptyMap();
    }
}
