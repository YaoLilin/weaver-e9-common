package com.customization.yll.common.web;

import java.lang.annotation.*;

/**
 * @author 姚礼林
 * @desc API 参数对象注解，可用于标记接口参数对象
 * @date 2025/12/19
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ApiModel {
    String description() default "";
}
