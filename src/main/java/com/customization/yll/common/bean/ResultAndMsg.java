package com.customization.yll.common.bean;

import lombok.ToString;

/**
 * @author 姚礼林
 * @desc 包含是否成功结果和信息字符串
 * @date 2025/7/7
 **/
@ToString
public class ResultAndMsg {
    private boolean success;
    private String msg;

    public ResultAndMsg(boolean success) {
        this.success = success;
    }

    public ResultAndMsg(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public ResultAndMsg() {
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
