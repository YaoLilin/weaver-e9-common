package com.customization.yll.common.workflow.entity;

import lombok.ToString;

/**
 * 流程审批信息
 * @author yaolilin
 */
@ToString
public class WorkflowApprovalInfoEntity {
    private Integer logId;
    private Integer operator;
    private Integer nodeId;
    private String operateDate;
    private String operateTime;
    private String remark;
    private Integer destNodeId;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(String operateDate) {
        this.operateDate = operateDate;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getDestNodeId() {
        return destNodeId;
    }

    public void setDestNodeId(Integer destNodeId) {
        this.destNodeId = destNodeId;
    }
}
