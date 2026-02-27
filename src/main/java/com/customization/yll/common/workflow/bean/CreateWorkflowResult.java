package com.customization.yll.common.workflow.bean;

import lombok.ToString;

/**
 * @author 姚礼林
 * @desc 创建流程返回结果
 * @date 2025/7/7
 **/
@ToString
public class CreateWorkflowResult {
    private Integer requestId;
    private boolean success;
    private String msg;

    public CreateWorkflowResult(Integer requestId, boolean success, String msg) {
        this.requestId = requestId;
        this.success = success;
        this.msg = msg;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
