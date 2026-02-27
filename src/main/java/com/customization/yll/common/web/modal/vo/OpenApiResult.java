package com.customization.yll.common.web.modal.vo;

import javax.ws.rs.core.Response;

/**
 * @author yaolilin
 * @desc 对外接口通用返回体
 * @date 2025/1/8
 **/
public class OpenApiResult<T> {
    private Integer status;
    private String message;
    private boolean success;
    private T data;

    public OpenApiResult(Integer status, String message, boolean success, T data) {
        this.status = status;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    /**
     * 接口请求成功返回对象
     * @param data 接口数据
     * @return 接口返回体
     * @param <T> 任意类型
     */
    public static <T> OpenApiResult<T> success(T data) {
        return new OpenApiResult<>(Response.Status.OK.getStatusCode(), "success", true, data);
    }

    /**
     * 接口请求失败返回对象
     * @param message 错误信息
     * @param status 对应HTTP的状态码
     * @return 接口返回体
     * @param <T> 任意类型
     */
    public static <T> OpenApiResult<T> failed(String message, Integer status) {
        return new OpenApiResult<>(status, message, false, null);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
