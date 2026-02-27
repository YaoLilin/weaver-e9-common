package com.customization.yll.common.util;


import weaver.workflow.request.RequestManager;

public class WorkflowActionUtil {

    private WorkflowActionUtil() {

    }

    public static void putUserFailedMsg(RequestManager requestManager, Class<?> c) {
        requestManager.setMessagecontent(String.format("执行 [%s] 流程Action出错，请联系管理员处理",c.getSimpleName()));
    }

    public static void putUserFailedMsg(String msg,RequestManager requestManager, Class<?> c) {
        requestManager.setMessagecontent(String.format("执行 [%s] 流程Action出错，%s",c.getSimpleName(),msg));
    }
}
