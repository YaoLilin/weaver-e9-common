package com.customization.yll.common.workflow.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 姚礼林
 * @desc 流程 action 参数
 * @date 2025/8/6
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ActionParam {
    boolean required() default  false;
    String displayName() default "";
    String desc() default  "";
    String defaultValue() default "";
}
