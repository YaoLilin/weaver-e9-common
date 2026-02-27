package com.customization.yll.common.workflow;

import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.IntegrationLog;
import com.customization.yll.common.service.GeneralTheadPoolService;
import com.customization.yll.common.util.WorkflowActionUtil;
import com.customization.yll.common.workflow.anotations.ActionParam;
import com.customization.yll.common.workflow.bean.ActionResult;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

import java.lang.reflect.Field;

/**
 * @author 姚礼林
 * @desc WorkflowAction 抽象类
 * @date 2025/8/6
 **/
public abstract class AbstractWorkflowAction implements Action {
    protected RequestInfo requestInfo;
    protected final IntegrationLog log = new IntegrationLog(this.getClass());
    @Setter
    protected WorkflowActionHelper actionHelper;

    @ActionParam(desc = "是否异步执行,0为否，1为是，默认否", defaultValue = "0")
    protected String async = "0";

    @Override
    public String execute(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
        log.info("执行 action ,请求id："+requestInfo.getRequestid());
        try {
            if (!verifyParams(requestInfo)) {
                return FAILURE_AND_CONTINUE;
            }
            this.actionHelper = new WorkflowActionHelper(requestInfo);
            if ("1".equals(async)) {
                GeneralTheadPoolService.INSTANCE.putTask(() -> asyncExecute(requestInfo));
                return SUCCESS;
            }
            ActionResult result = doExecute(requestInfo);
            if (result.isSuccess()) {
                log.info("Action 执行成功:{}", result.getMsg());
                return SUCCESS;
            }
            log.error("Action 执行错误，错误信息:{}", result.getMsg());
            return StrUtil.isNotEmpty(result.getMsg()) ? failed(result.getMsg()) : failed();
        } catch (Exception e) {
            return WorkflowActionExceptionHandle.handle(requestInfo, e, this.getClass());
        }
    }

    /**
     * 执行 action
     * @param requestInfo requestInfo
     * @return 执行结构
     */
    protected abstract @NotNull ActionResult doExecute(RequestInfo requestInfo);

    private String failed() {
        if (requestInfo != null) {
            WorkflowActionUtil.putUserFailedMsg(requestInfo.getRequestManager(), this.getClass());
        }
        return FAILURE_AND_CONTINUE;
    }

    private String failed(String msg) {
        if (requestInfo != null) {
            WorkflowActionUtil.putUserFailedMsg(msg, requestInfo.getRequestManager(), this.getClass());
        }
        return FAILURE_AND_CONTINUE;
    }

    /**
     * 校验带有 @ActionParam 注解的参数
     * @return 校验通过返回 true，不通过返回 false
     */
    private boolean verifyParams(RequestInfo requestInfo) {
        Class<?> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ActionParam.class)) {
                ActionParam actionParam = field.getAnnotation(ActionParam.class);

                if (actionParam.required()) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        if (value == null) {
                            WorkflowActionUtil.putUserFailedMsg(String.format("Action 参数不正确，" +
                                    "[%s] 参数必填，请检查 Action 参数配置",field.getName()),
                                    requestInfo.getRequestManager(),this.getClass());
                            return false;
                        }
                    } catch (IllegalAccessException e) {
                        log.error("action 参数校验异常", e);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void asyncExecute(RequestInfo requestInfo) {
        try {
            log.info("异步调用 Action，请求id：" + requestInfo.getRequestid());
            ActionResult result = doExecute(requestInfo);
            if (result.isSuccess()) {
                log.info("Action 执行成功，请求id:{}", requestInfo.getRequestid());
            }else {
                log.error("Action 执行失败，信息：{}", result.getMsg());
            }
        } catch (Exception e) {
            WorkflowActionExceptionHandle.handle(requestInfo, e, this.getClass());
        }
    }
}
