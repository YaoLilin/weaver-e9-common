package com.customization.yll.common.workflow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.customization.yll.common.bean.ResultAndMsg;
import com.customization.yll.common.util.WorkflowUtil;
import com.customization.yll.common.workflow.bean.CreateWorkflowResult;
import com.customization.yll.common.workflow.bean.DetailInfo;
import com.engine.workflow.biz.publicApi.RequestOperateBiz;
import com.engine.workflow.entity.core.RequestInfoEntity;
import com.engine.workflow.entity.publicApi.ReqOperateRequestEntity;
import com.engine.workflow.entity.publicApi.WorkflowDetailTableInfoEntity;
import com.engine.workflow.entity.requestForm.ReqFlowFailMsgEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.workflow.request.RequestDeleteUtils;
import weaver.workflow.webservices.WorkflowRequestTableField;
import weaver.workflow.webservices.WorkflowRequestTableRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 用于创建流程
 * @date 2023/9/12
 */
public class WorkflowCreator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 创建流程
     *
     * @param mainData     主表数据
     * @param detailData   明细数据
     * @param workflowId   流程id
     * @param requestName  流程请求名称
     * @param creator      流程创建人
     * @param isAutoSubmit 是否提交到下个节点
     * @return 返回结果
     */
    public CreateWorkflowResult create(Map<String, String> mainData, @Nullable List<DetailInfo> detailData,
                                       int workflowId,@Nullable String requestName, int creator, boolean isAutoSubmit) {
        try {
            return this.createWorkflow(mainData, detailData, workflowId, requestName, creator, isAutoSubmit);
        } catch (Exception e) {
            log.error("创建流程失败", e);
            return new CreateWorkflowResult(null, false, "创建流程发生异常：" + e.getMessage());
        }
    }

    private CreateWorkflowResult createWorkflow(@NotNull Map<String, String> mainData,
                                                @Nullable List<DetailInfo> detailData,
                                               int workflowId, @Nullable String requestName,
                                               int creator, boolean isAutoSubmit) {
        log.info("workflowId:" + workflowId + ",requestName:" + requestName + "," +
                "creator:" + creator + ",isAutoSubmit:" + isAutoSubmit);
        User user = new User(creator);
        // 准备流程参数，流程主表和明细数据
        ReqOperateRequestEntity operateEntity = prepare(mainData, detailData, workflowId, requestName, isAutoSubmit);
        // 创建流程
        RequestInfoEntity entity = RequestOperateBiz.initRequestInfo(operateEntity, user);
        int requestId = RequestOperateBiz.initRequestBaseInfo(user, entity, operateEntity);
        if (requestId < 0) {
            log.error("创建流程错误,requestId < 0");
            return new CreateWorkflowResult(null, false, "创建流程错误，初始化请求信息失败");
        }
        log.info("流程已创建，请求id：" + requestId);
        entity.setRequestId(String.valueOf(requestId));
        // 保存流程
        ResultAndMsg saveResult = saveWorkflow(entity, user, operateEntity);
        if (!saveResult.isSuccess()) {
            return new CreateWorkflowResult(requestId, false, "流程保存失败:" + saveResult.getMsg());
        }
        if (isAutoSubmit) {
            // 提交流程
            entity.setSrc("submit");
            if (!RequestOperateBiz.flowNextNode(entity, user, operateEntity, new ReqFlowFailMsgEntity())) {
                log.error("自动提交失败");
                return new CreateWorkflowResult(requestId, false, "创建流程成功，但提交失败");
            }
        }
        return new CreateWorkflowResult(requestId, true, "创建成功");
    }

    @NotNull
    private ReqOperateRequestEntity prepare(@NotNull Map<String, String> mainData, @Nullable List<DetailInfo> detailData,
                                            int workflowId, @Nullable String requestName, boolean isAutoSubmit) {
        ReqOperateRequestEntity operateEntity = new ReqOperateRequestEntity();
        List<WorkflowRequestTableField> mainDataList = addMainFieldData(mainData);
        log.info("已添加主表字段");
        if (CollUtil.isNotEmpty(detailData)) {
            List<WorkflowDetailTableInfoEntity> detailTableInfo = addDetailData(detailData, workflowId);
            operateEntity.setDetailData(detailTableInfo);
        }
        // 添加流程信息
        operateEntity.setMainData(mainDataList);
        operateEntity.setWorkflowId(workflowId);
        if (StrUtil.isNotBlank(requestName)) {
            operateEntity.setRequestName(requestName);
        }else {
            String workflowName = WorkflowUtil.getWorkflowName(workflowId, new RecordSet());
            operateEntity.setRequestName(workflowName);
        }
        operateEntity.setRequestId(-1);
        if (isAutoSubmit) {
            Map<String, Object> otherParam = new HashMap<>(1);
            otherParam.put("isnextflow", "0");
            operateEntity.setOtherParams(otherParam);
        }
        return operateEntity;
    }

    private ResultAndMsg saveWorkflow(RequestInfoEntity entity, User user, ReqOperateRequestEntity operateEntity) {
        try {
            Map<String, Object> saveResult = RequestOperateBiz.saveRequestInfo(entity, user, operateEntity,
                    new ReqFlowFailMsgEntity());
            log.info("保存流程结果：" + saveResult);
            if (!saveResult.isEmpty()) {
                log.error("创建流程错误，saveResult.size() > 0，saveResult：" + saveResult);
                return new ResultAndMsg(false, JSON.toJSONString(saveResult));
            }
            return new ResultAndMsg(true, "");
        } catch (Exception e) {
            log.error("流程保存失败：" + e.getMessage());
            deleteRequest(new HashMap<>(1), user, entity);
            return new ResultAndMsg(false, e.getMessage());
        }
    }

    @NotNull
    private List<WorkflowRequestTableField> addMainFieldData(@NotNull Map<String, String> mainData) {
        // 添加主表字段
        log.info("主表数据：" + JSON.toJSONString(mainData));
        List<WorkflowRequestTableField> mainDataList = new ArrayList<>();
        for (Map.Entry<String, String> entry : mainData.entrySet()) {
            WorkflowRequestTableField field = new WorkflowRequestTableField();
            field.setFieldName(entry.getKey());
            field.setFieldValue(entry.getValue());
            mainDataList.add(field);
        }
        return mainDataList;
    }

    private List<WorkflowDetailTableInfoEntity> addDetailData(List<DetailInfo> detailData, int workflowId) {
        log.info("明细数据：" + JSON.toJSONString(detailData));
        String workflowTableName = WorkflowUtil.getWorkflowTableName(workflowId, new RecordSet());
        List<WorkflowDetailTableInfoEntity> details = new ArrayList<>();
        // 生成每个明细表的数据
        for (DetailInfo detail : detailData) {
            String detailTableName = workflowTableName + "_dt" + (detail.getIndex() + 1);
            List<WorkflowRequestTableRecord> rows = new ArrayList<>();
            // 生成每个明细行的数据
            for (Map<String ,String > fieldData : detail.getDetailData()) {
                WorkflowRequestTableRecord row = new WorkflowRequestTableRecord();
                List<WorkflowRequestTableField> fields = new ArrayList<>();
                for (Map.Entry<String, String> entry : fieldData.entrySet()) {
                    String fieldName = entry.getKey();
                    String value = entry.getValue();
                    WorkflowRequestTableField field = new WorkflowRequestTableField();
                    field.setFieldName(fieldName);
                    field.setFieldValue(value);
                    fields.add(field);
                }
                row.setWorkflowRequestTableFields(fields.toArray(new WorkflowRequestTableField[0]));
                rows.add(row);
            }
            WorkflowDetailTableInfoEntity tableInfoEntity = new WorkflowDetailTableInfoEntity();
            tableInfoEntity.setTableDBName(detailTableName);
            tableInfoEntity.setWorkflowRequestTableRecords(rows.toArray(new WorkflowRequestTableRecord[0]));
            details.add(tableInfoEntity);
        }

        return details;
    }


    private void deleteRequest(Map<String, Object> otherParams, User user, RequestInfoEntity requestInfoEntity) {
        if ("1".equals(Util.null2s(Util.null2String(otherParams.get("delReqFlowFaild")), "1"))) {
            RequestDeleteUtils var3 = new RequestDeleteUtils();
            var3.requestDelete(user, "from_restful", Util.getIntValue(requestInfoEntity.getRequestId()));
        }
    }
}
