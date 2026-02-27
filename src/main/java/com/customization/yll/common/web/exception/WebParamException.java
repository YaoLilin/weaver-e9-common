package com.customization.yll.common.web.exception;

import com.customization.yll.common.exception.FrontErrorMessage;

import javax.ws.rs.core.Response;

/**
 * @author 姚礼林
 * @desc 接口参数异常
 * @date 2024/6/6
 */
public class WebParamException extends RuntimeException implements FrontErrorMessage {
    private final Response.Status status;

    public Response.Status getStatus() {
        return status;
    }

    public WebParamException(String message) {
        super(message);
        this.status = Response.Status.BAD_REQUEST;
    }
    public WebParamException(Response.Status status,String message) {
        super(message);
        this.status = status;
    }

    public WebParamException() {
        this.status = Response.Status.BAD_REQUEST;
    }

    public static class BodyParamException extends WebParamException {
        public BodyParamException(String message) {
            super(message);
        }
        public BodyParamException(){}

    }

    public static class QueryParamException extends WebParamException {
        public QueryParamException(String message) {
            super(Response.Status.NOT_FOUND, message);
        }

        public QueryParamException() {
            super(Response.Status.NOT_FOUND,null);
        }
    }

    public static class HeaderParamException extends WebParamException {
        public HeaderParamException(String message) {
            super( message);
        }

        public HeaderParamException() {
        }
    }

}
