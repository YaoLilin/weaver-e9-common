package com.customization.yll.common.workflow;

import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.exception.FieldValueEmptyException;
import com.customization.yll.common.exception.FrontErrorMessage;
import com.customization.yll.common.util.FormUtil;
import com.customization.yll.common.util.WorkflowActionUtil;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;
import weaver.workflow.request.RequestManager;

/**
 * 流程action执行异常处理，主要是给前端用户提示错误
 * @author yaolilin
 */
public class WorkflowActionExceptionHandle {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowActionExceptionHandle.class);
    private static final String MSG_FORMAT = "执行 [%s] 流程Action出错，msg:%s";

    private WorkflowActionExceptionHandle() {

    }

    /**
     * 处理流程 Action 异常，给前端提示错误
     * @param requestInfo requestInfo
     * @param e 异常
     * @param c 当前Action类
     * @return actin 错误标识，中断流程提交
     */
    public static String handle(RequestInfo requestInfo, Throwable e,Class<?> c) {
        return handle(requestInfo.getRequestManager(), e, c);
    }

    /**
     * 处理流程 Action 异常，给前端提示错误
     * @param requestManager requestManager
     * @param e 异常
     * @param c 当前Action类
     * @return actin 错误标识，中断流程提交
     */
    public static String handle(RequestManager requestManager, Throwable e, Class<?> c) {
        logger.error(String.format(MSG_FORMAT, c.getSimpleName(),e.getMessage()),e);
        if (e instanceof FieldValueEmptyException) {
            return handleFieldEmptyException(requestManager, (FieldValueEmptyException) e, c);
        }else if (e instanceof FrontErrorMessage) {
            WorkflowActionUtil.putUserFailedMsg(e.getLocalizedMessage(),requestManager,c);
        }else {
            WorkflowActionUtil.putUserFailedMsg(requestManager,c);
        }
        return Action.FAILURE_AND_CONTINUE;
    }

    /**
     * 处理流程 Action 异常，给前端提示错误
     * @param msg 给前端的错误信息
     * @param requestInfo requestInfo
     * @param e 异常
     * @param c 当前Action类
     * @return actin 错误标识，中断流程提交
     */
    public static String handle(String msg,RequestInfo requestInfo, Throwable e,Class<?> c) {
        return handle(msg,requestInfo.getRequestManager(), e, c);
    }

    /**
     * 处理流程 Action 异常，给前端提示错误
     * @param msg 给前端的错误信息
     * @param requestManager requestManager
     * @param e 异常
     * @param c 当前Action类
     * @return actin 错误标识，中断流程提交
     */
    public static String handle(String msg,RequestManager requestManager, Throwable e,Class<?> c) {
        logger.error(String.format(MSG_FORMAT, c.getSimpleName(),e.getMessage()),e);
        WorkflowActionUtil.putUserFailedMsg(msg,requestManager,c);
        return Action.FAILURE_AND_CONTINUE;
    }

    /**
     * 处理表单字段值为空异常，向用户显示字段必填错误信息
     * @param requestInfo requestInfo
     * @param e FieldValueEmptyException
     * @param c Action class
     * @return actin 错误标识，中断流程提交
     */
    public static String handleFieldEmptyException(RequestInfo requestInfo, FieldValueEmptyException e,
                                                 Class<?> c) {
        return handleFieldEmptyException(requestInfo.getRequestManager(), e, c);
    }

    /**
     * 处理表单字段值为空异常，向用户显示字段必填错误信息
     * @param requestManager RequestManager
     * @param e FieldValueEmptyException
     * @param c Action class
     * @return actin 错误标识，中断流程提交
     */
    public static String handleFieldEmptyException(RequestManager requestManager, FieldValueEmptyException e,
                                                 Class<?> c) {
        RecordSet recordSet = new RecordSet();
        if (StrUtil.isNotEmpty(e.getFieldName())) {
            String fieldLabelName = FormUtil.getFieldName(e.getFieldName(), requestManager.getWorkflowid(),
                    null, recordSet);
            WorkflowActionExceptionHandle.handle(String.format("[%s] 字段无值，该字段必填", StrUtil.isBlank(fieldLabelName)
                            ? e.getFieldName() : fieldLabelName),
                    requestManager, e, c);
        }else {
            WorkflowActionExceptionHandle.handle(e.getLocalizedMessage(), requestManager, e, c);
        }
        return Action.FAILURE_AND_CONTINUE;
    }
}
