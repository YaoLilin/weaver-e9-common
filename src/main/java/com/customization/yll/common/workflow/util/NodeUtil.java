package com.customization.yll.common.workflow.util;

import lombok.experimental.UtilityClass;
import weaver.conn.RecordSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 流程节点工具类
 * @date 2025/4/8
 **/
@UtilityClass
public class NodeUtil {

    public static String getNodeName(int nodeId, RecordSet recordSet) {
        recordSet.executeQuery("select nodename from workflow_nodebase where id=?", nodeId);
        recordSet.next();
        return recordSet.getString("nodename");
    }

    public static Optional<Integer> getNodeId(int nodeName, int workflowId, RecordSet recordSet) {
        recordSet.executeQuery("SELECT n.id from workflow_nodebase n join workflow_flownode f " +
                "on f.nodeid = n.id where f.WORKFLOWID=? and n.nodename=?", workflowId, nodeName);
        if (!recordSet.next()) {
            return Optional.empty();
        }
        return Optional.of(recordSet.getInt("id"));
    }

    /**
     * 根据节点名称获取所有流程版本的节点id
     * @param formId 表单id
     * @param nodeName 节点名称
     * @param recordSet recordSet
     * @return 所有流程版本的节点id
     */
    public static List<Integer> getNodeListByNodeName(int formId,String nodeName,RecordSet recordSet) {
        recordSet.executeQuery("SELECT n.id from workflow_nodebase n join workflow_flownode f " +
                "on f.nodeid = n.id join workflow_base w on f.WORKFLOWID = w.id " +
                "where w.formid=? and n.nodename=?", formId, nodeName);
        List<Integer> result = new ArrayList<>();
        while (recordSet.next()) {
            result.add(recordSet.getInt("id"));
        }
        return result;
    }
}
