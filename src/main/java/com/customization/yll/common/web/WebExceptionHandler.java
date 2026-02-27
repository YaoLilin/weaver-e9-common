package com.customization.yll.common.web;

import com.customization.yll.common.exception.FrontErrorMessage;
import com.customization.yll.common.web.exception.WebParamException;
import com.customization.yll.common.web.modal.vo.ApiResult;
import com.customization.yll.common.web.modal.vo.OpenApiResult;
import lombok.experimental.UtilityClass;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 * @author 姚礼林
 * @desc 接口异常处理，将异常
 * @date 2024/6/11
 */
@UtilityClass
public class WebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    public static Response handle(Throwable e){
        logger.error("接口发生异常",e);
        if (e instanceof WebParamException) {
            WebParamException webParamException = (WebParamException) e;
            return Response.status(webParamException.getStatus())
                    .entity(ApiResult.failed(e.getMessage()))
                    .build();
        }
        if (e instanceof FrontErrorMessage) {
            return Response.serverError()
                    .entity(ApiResult.failed("发生错误：" + e.getMessage()))
                    .build();
        }
        return Response.serverError()
                .entity(ApiResult.failed("服务器内部错误"))
                .build();
    }

    public static Response handleOpenApiException(Throwable e){
        logger.error("接口发生异常：" + e.getMessage(), e);
        if (e instanceof WebParamException) {
            WebParamException webParamException = (WebParamException) e;
            return Response.ok()
                    .entity(OpenApiResult.failed(e.getMessage(), webParamException.getStatus().getStatusCode()))
                    .build();
        }
        return Response.ok()
                .entity(OpenApiResult.failed("接口发生异常",
                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                .build();
    }
}
