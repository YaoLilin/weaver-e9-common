package com.engine.interfaces.yll.common.web;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.customization.yll.common.IntegrationLog;
import com.customization.yll.common.web.WebExceptionHandler;
import com.customization.yll.common.web.modal.vo.ApiResult;
import org.apache.log4j.Level;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 集成日志接口
 */
public class IntegrationLogAction {

    /**
     * 修改日志级别
     */
    @Path("/level")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setLogLevel(@FormParam("level") String level) {
        try {
            if (StrUtil.isBlank(level)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResult.failed("level 不能为空")).build();
            }
            Level levelObj = Level.toLevel(level);
            if (levelObj == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResult.failed("level 格式错误")).build();
            }
            IntegrationLog.logLevel = levelObj;
            return Response.ok().entity(ApiResult.success("修改日志级别成功")).build();
        } catch (Exception e) {
            return WebExceptionHandler.handle(e);
        }
    }

    @Path("/level")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLogLevel() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("level", IntegrationLog.logLevel.toString());
            return Response.ok().entity(ApiResult.success(jsonObject)).build();
        } catch (Exception e) {
            return WebExceptionHandler.handle(e);
        }

    }
}
