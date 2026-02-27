package com.customization.yll.common.workflow;

import cn.hutool.core.collection.CollUtil;
import com.customization.yll.common.IntegrationLog;
import com.customization.yll.common.workflow.constants.WorkflowLogType;
import com.customization.yll.common.workflow.entity.WorkflowApprovalInfoEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import weaver.conn.RecordSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 获取流程审批信息（签字意见日志）
 *
 * @author yaolilin
 */
public class WorkflowApprovalInfoManager {
    private final IntegrationLog log = new IntegrationLog(this.getClass());
    private final RecordSet recordSet = new RecordSet();

    /**
     * 获取流程签字意见日志信息，默认获取提交和审批类型的日志，忽略系统管理员日志
     *
     * @return 流程签字意见日志信息
     */
    public List<WorkflowApprovalInfoEntity> getWorkflowApprovalInfo(int requestId) {
        return getWorkflowApprovalInfo(null, true, requestId);
    }

    /**
     * 获取流程签字意见日志信息
     * @param logTypes 只查询指定的日志中的节点操作类型，如果为 null 或 空列表则不指定，获取全部操作类型日志
     * @param ignoreAdmin 是否忽略系统管理员的日志
     * @param requestId 请求id
     * @return 流程签字意见日志信息
     */
    public List<WorkflowApprovalInfoEntity> getWorkflowApprovalInfo(@Nullable List<WorkflowLogType> logTypes,
                                                                    boolean ignoreAdmin, int requestId) {
        return getWorkflowApprovalInfo(logTypes, ignoreAdmin, requestId,"");
    }

    /**
     * 获取流程签字意见日志信息
     * @param logTypes 只查询指定的日志中的节点操作类型，如果为 null 或 空列表则不指定，获取全部操作类型日志
     * @param ignoreAdmin 是否忽略系统管理员的日志
     * @param requestId 请求id
     * @param nodeIds 指定节点id
     * @return 流程签字意见日志信息
     */
    public List<WorkflowApprovalInfoEntity> getWorkflowApprovalInfo(@Nullable List<WorkflowLogType> logTypes,
                                                                    boolean ignoreAdmin, int requestId,
                                                                    List<Integer> nodeIds) {
        String nodeIdsCondition = "";
        if (CollUtil.isNotEmpty(nodeIds)) {
            nodeIdsCondition = "and nodeid in (" + CollUtil.join(nodeIds, ",") + ") ";
        }

        return getWorkflowApprovalInfo(logTypes, ignoreAdmin, requestId, nodeIdsCondition);
    }

    /**
     * 获取流程签字意见日志信息
     * @param logTypes 只查询指定的日志中的节点操作类型，如果为 null 或 空列表则不指定，获取全部操作类型日志
     * @param ignoreAdmin 是否忽略系统管理员的日志
     * @param requestId 请求id
     * @param sqlCondition 自定义sql查询条件，条件开始需带上 AND
     * @return 流程签字意见日志信息
     */
    public List<WorkflowApprovalInfoEntity> getWorkflowApprovalInfo(@Nullable List<WorkflowLogType> logTypes,
                                                                    boolean ignoreAdmin, int requestId,
                                                                    String sqlCondition) {
        List<WorkflowApprovalInfoEntity> infoList = new ArrayList<>();
        String logTypeCondition = getLogTypeCondition(logTypes);
        String sql = "select logid,operator,nodeid,operatedate,operatetime,remark,destnodeid" +
                " from workflow_requestlog " +
                "WHERE requestid=? " + logTypeCondition + sqlCondition + " order by logid asc";
        if (!recordSet.executeQuery(sql, requestId)) {
            log.error("sql查询错误，sql:" + sql);
            return Collections.emptyList();
        }
        while (recordSet.next()) {
            WorkflowApprovalInfoEntity info = new WorkflowApprovalInfoEntity();
            int operator = recordSet.getInt("operator");
            // 忽略系统管理员操作
            if (ignoreAdmin && operator == 1) {
                continue;
            }
            info.setLogId(recordSet.getInt("logid"));
            info.setOperator(operator);
            info.setNodeId(recordSet.getInt("nodeid"));
            info.setOperateDate(recordSet.getString("operatedate"));
            info.setOperateTime(recordSet.getString("operatetime"));
            info.setRemark(recordSet.getString("remark"));
            info.setDestNodeId(recordSet.getInt("destnodeid"));
            infoList.add(info);
        }
        return infoList;
    }


    @NotNull
    private static String getLogTypeCondition(List<WorkflowLogType> logTypes) {
        StringBuilder logTypeCondition = new StringBuilder();
        if (CollUtil.isNotEmpty(logTypes)) {
            logTypeCondition.append("AND logtype in (");
            for (WorkflowLogType logType : logTypes) {
                logTypeCondition.append("'").append(logType.getValue()).append("',");
            }
            logTypeCondition.deleteCharAt(logTypeCondition.length() - 1);
            logTypeCondition.append(")");
        }
        return logTypeCondition.toString();
    }
}
