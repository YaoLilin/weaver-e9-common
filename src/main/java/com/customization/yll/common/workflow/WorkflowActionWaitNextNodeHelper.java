package com.customization.yll.common.workflow;

import com.customization.yll.common.util.WorkflowUtil;
import weaver.conn.RecordSet;

/**
 * @author 姚礼林
 * @desc 在流程Action异步提交时可等待到达下个节点
 * @date 2025/9/19
 **/
public class WorkflowActionWaitNextNodeHelper {
    private final RecordSet recordSet;

    public WorkflowActionWaitNextNodeHelper(RecordSet recordSet) {
        this.recordSet = recordSet;
    }

    /**
     * 等待到达下个节点,注意执行此方法时线程会进入阻塞，直到到达下个节点或者超过最大等待时间
     * @param maxWaitTime 最大等待时间，如果超过则不再等待到达下个节点
     * @param requestId 请求id
     * @param nextNodeId 下个节点id
     * @return 是否到达下个节点
     */
    public boolean waitFlowNextNode(int maxWaitTime, int requestId, int nextNodeId  ) {
        long waitTimeMillis = 0;
        boolean isFlowTargetNode = false;
        // 循环等待流程流转到下个节点，如果超过30秒流程还未到下个节点则取消等待
        while (waitTimeMillis <= maxWaitTime) {
            int currentNodeId = WorkflowUtil.getCurrentNodeId(requestId, recordSet);
            if (currentNodeId == nextNodeId) {
                isFlowTargetNode = true;
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            waitTimeMillis+=200;
        }
        return isFlowTargetNode;
    }

}
