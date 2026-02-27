package com.customization.yll.common.util;

import com.customization.yll.common.IntegrationLog;
import lombok.experimental.UtilityClass;

/**
 * @author 姚礼林
 * @desc log 日志工具
 * @date 2025/7/28
 **/
@UtilityClass
public class LogUtil {

    /**
     * 获取集成日志对象
     * @param cls 调用类对象
     * @return 集成日志对象
     */
    public static IntegrationLog getIntegrationLog(Class<?> cls) {
        return new IntegrationLog(cls);
    }

}
