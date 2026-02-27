package com.customization.yll.common.web.exception;

import com.customization.yll.common.exception.FrontErrorMessage;

/**
 * @author 姚礼林
 * @desc 接口返回结果校验不通过
 * @date 2024/6/6
 */
public class ApiInvalidResultException extends Exception implements FrontErrorMessage {
    public ApiInvalidResultException(String message){
        super(message);
    }
}
