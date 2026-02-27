package com.customization.yll.common.workflow.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.engine.common.util.ServiceUtil;
import com.engine.workflow.constant.PAResponseCode;
import com.engine.workflow.entity.publicApi.PAResponseEntity;
import com.engine.workflow.entity.publicApi.ReqOperateRequestEntity;
import com.engine.workflow.publicApi.WorkflowRequestOperatePA;
import com.engine.workflow.publicApi.impl.WorkflowRequestOperatePAImpl;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import weaver.hrm.User;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

/**
 * @author yaolilin
 * @desc 流程操作工具类
 * @date 2025/1/3
 **/
@UtilityClass
public class WorkflowOperateUtil {
    private final Logger log = LoggerFactory.getLogger(WorkflowOperateUtil.class);

    /**
     * 提交流程
     * @param requestId 请求id
     * @param operatorId 操作者id
     * @param remark 签字意见，如果不需要则传null或者空字符串
     * @return 是否提交成功
     */
    public static boolean submit(int requestId,int operatorId,@Nullable String remark) {
        ReqOperateRequestEntity operateEntity = new ReqOperateRequestEntity();
        if (StrUtil.isNotEmpty(remark)) {
            operateEntity.setRemark(remark);
        }
        operateEntity.setRequestId(requestId);
        WorkflowRequestOperatePA operatePa = ServiceUtil.getService(WorkflowRequestOperatePAImpl.class);
        PAResponseEntity result = operatePa.submitRequest(new User(operatorId), operateEntity);
        log.info("提交结果："+ JSON.toJSONString(result));
        return result.getCode() == PAResponseCode.SUCCESS;
    }

    /**
     * 流程干预，请参考
     * <a href="https://www.e-cology.com.cn/sp/ebdfpage/card/0/100002760000000773/100594140000015321">流程干预</a>
     * @param user 操作者
     * @param operateEntity 参数，必填：submitNodeId（目标节点）、Intervenorid（干预节点接收人，多个人以","号隔开）、requestId
     * @return 当 code 为 SUCCESS 时表示流程干预成功
     */
    public static PAResponseEntity doIntervenor(User user, ReqOperateRequestEntity operateEntity) {
        return ServiceUtil.getService(WorkflowRequestOperatePAImpl.class)
                .doIntervenor(user, operateEntity);
    }
}
