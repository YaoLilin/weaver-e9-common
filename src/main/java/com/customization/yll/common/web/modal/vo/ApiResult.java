package com.customization.yll.common.web.modal.vo;

import com.customization.yll.common.web.constants.ApiResultStatus;

/**
 * @author 姚礼林
 * @desc 接口通用返回体
 * @date 2024/6/11
 */
public class ApiResult<T>{
    private String status;
    private T data;
    private String message;
    private String errorType;
    private boolean success;

    public ApiResult(String status, T data, String message,String errorType) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.errorType = errorType;
        if (ApiResultStatus.SUCCESS.equals(status)) {
            this.success = true;
        } else {
            this.success = false;
        }
    }

    public ApiResult(boolean success, T data, String message, String errorType) {
        this.data = data;
        this.message = message;
        this.errorType = errorType;
        this.success = success;
        if (success) {
            this.status = ApiResultStatus.SUCCESS;
        } else {
            this.status = ApiResultStatus.FAILED;
        }
    }

    public ApiResult() {
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(true, data, null, null);
    }

    public static <T> ApiResult<T> success(T data, String message) {
        return new ApiResult<>(true, data, message, null);
    }

    public static <T> ApiResult<T> failed(String message,String errorType) {
        return new ApiResult<>(false, null, message, errorType);
    }

    public static <T> ApiResult<T> failed(String message) {
        return new ApiResult<>(false, null, message, null);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
