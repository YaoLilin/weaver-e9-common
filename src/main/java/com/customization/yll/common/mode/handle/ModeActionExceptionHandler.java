package com.customization.yll.common.mode.handle;

import com.customization.yll.common.mode.util.ModeActionUtil;
import lombok.experimental.UtilityClass;
import weaver.formmode.customjavacode.AbstractModeExpandJavaCodeNew;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.util.Map;

/**
 * @author yaolilin
 * @desc 建模action异常处理
 * @date 2024/8/20
 **/
@UtilityClass
public class ModeActionExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ModeActionExceptionHandler.class);
    public static Map<String, String> handle(Throwable e, Class<? extends AbstractModeExpandJavaCodeNew> c) {
        logger.error(String.format("执行 [%s] Action出错，msg:%s", c.getSimpleName(),e.getMessage()),e);
        return ModeActionUtil.failed(c);
    }
    public static Map<String, String> handle(String msg, Throwable e, Class<? extends AbstractModeExpandJavaCodeNew> c) {
        logger.error(String.format("执行 [%s] 流程Action出错，msg:%s", c.getSimpleName(),e.getMessage()),e);
        return ModeActionUtil.failed(msg,c);
    }
}
