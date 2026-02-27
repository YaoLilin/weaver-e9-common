package com.customization.yll.common.workflow.bean;

import lombok.ToString;

/**
 * @author 姚礼林
 * @desc 流程 Action 执行结果
 * @date 2025/8/7
 **/
@ToString
public class ActionResult {
    private boolean success;
    private String msg;

    public ActionResult(boolean success) {
        this.success = success;
    }

    public ActionResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
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
