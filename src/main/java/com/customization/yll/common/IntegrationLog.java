package com.customization.yll.common;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import weaver.integration.logging.Log4JLogger;

/**
 * @author 姚礼林
 * @desc 集成日志，对标准集成日志的扩展，支持使用占位符日志输出
 * @date 2025/8/6
 **/
public class IntegrationLog extends Log4JLogger {
    public static final String INTEGRATION = "integration";
    private final Logger log;


    public IntegrationLog(Class<?> cls) {
        this.setClassname(cls.getCanonicalName());
        this.init(INTEGRATION);
        log = org.apache.log4j.Logger.getLogger(INTEGRATION);
    }

    public IntegrationLog(String logName) {
        this.setClassname(logName);
        this.init(INTEGRATION);
        log = org.apache.log4j.Logger.getLogger(INTEGRATION);
    }

    public void debug(String  o, Object ...params) {
        FormattingTuple ft = MessageFormatter.arrayFormat(o, params);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        this.log.debug(getMessage(methodName, ft), ft.getThrowable());
    }

    public void info(String  o,Object ...params) {
        FormattingTuple ft = MessageFormatter.arrayFormat(o, params);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        this.log.info(getMessage(methodName, ft), ft.getThrowable());
    }

    public void error(String o, Object... params) {
        FormattingTuple ft = MessageFormatter.arrayFormat(o, params);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        this.log.error(getMessage(methodName, ft), ft.getThrowable());
    }

    public void warn(String o, Object... params) {
        FormattingTuple ft = MessageFormatter.arrayFormat(o, params);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        this.log.warn(getMessage(methodName, ft), ft.getThrowable());
    }

    public String toJsonStr(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    @NotNull
    private String getMessage(String methodName, FormattingTuple ft) {
        return getClassname() + "." + methodName + "() - " + ft.getMessage();
    }
}
