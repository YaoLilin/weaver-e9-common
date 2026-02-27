package com.customization.yll.common.util;

import com.api.formmode.page.util.Util;
import com.customization.yll.common.bean.WorkflowNodeInfo;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.soa.workflow.request.*;
import weaver.workflow.workflow.WorkflowVersion;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程工具类
 * @author yaolilin
 */
@UtilityClass
public class WorkflowUtil {

    private static final Logger log = LoggerFactory.getLogger(WorkflowUtil.class);

    /**
     * 获取指定主表字段
     *
     * @param requestInfo requestInfo
     * @param fieldName   要获取字段的字段名
     * @return 返回字段的值
     */
    public static String getMainFieldValue(RequestInfo requestInfo, String fieldName) {
        String fieldValue = "";
        //取主表字段集合
        Property[] properties = requestInfo.getMainTableInfo().getProperty();
        // 遍历主表字段
        for (Property property : properties) {
            // 主字段名称
            String name = property.getName();
            // 主字段对应的值
            String value = Util.null2String(property.getValue());
            if (name.equalsIgnoreCase(fieldName)) {
                fieldValue = value;
                break;
            }
        }
        return fieldValue;
    }

    /**
     * 获取指定主表字段
     * @param requestId 请求id
     * @param fieldName 字段名
     * @param recordSet recordSet
     * @return 字段值
     */
    public static String getMainFieldValue(int requestId, String fieldName,RecordSet recordSet) {
        int workflowId = getWorkflowId(requestId,recordSet);
        String tableName = getWorkflowTableName(workflowId, recordSet);
        return getTableFieldValue(requestId, fieldName,tableName, recordSet);
    }

    /**
     * 获取指定主表字段
     * @param requestId 请求id
     * @param formId 表单id
     * @param fieldName 流程字段名称
     * @param recordSet recordSet
     * @return 字段值
     */
    public static String getMainFieldValue(int requestId, int formId,String fieldName,RecordSet recordSet) {
        String tableName = FormUtil.getFormTableName(formId,recordSet);
        return getTableFieldValue(requestId, fieldName,tableName, recordSet);
    }

    /**
     * 更新流程主表字段
     * @param fieldData 字段数据
     * @param requestInfo requestInfo
     * @return 是否成功
     */
    public static boolean updateMainFieldValue(Map<String, Object> fieldData, RequestInfo requestInfo) {
        RecordSet recordSet = new RecordSet();
        Map<String, Object> conditions = new HashMap<>(1);
        conditions.put("requestid", requestInfo.getRequestid());
        return DbUtil.update(fieldData, conditions, requestInfo.getRequestManager().getBillTableName(), recordSet);
    }

    /**
     * 获取明细数据，将会根据 fieldNames 参数所指定的字段名称返回明细中该字段值的集合
     *
     * @param detailTable detailTable
     * @param fieldNames  要获取明细字段的字段名
     * @return 返回明细数据，一个元素表示一行明细数据
     */
    public static List<Map<String, String>> getDetailData(DetailTable detailTable, String[] fieldNames) {
        // 存放明细数据，每一行对应一个map对象，map存放字段名对应的字段值
        List<Map<String, String>> detailList = new ArrayList<>();
        // 获取明细所有行
        Row[] s = detailTable.getRow();
        // 遍历明细行
        for (Row r : s) {
            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("id", r.getId());
            // 获取明细列
            Cell[] c = r.getCell();
            // 遍历列数据
            for (Cell c1 : c) {
                // 明细字段名称
                String fieldName = c1.getName();
                // 明细字段的值
                String value = c1.getValue();
                // 获取想要的字段
                for (String name : fieldNames) {
                    // 判断该列的字段是否是想要获取的字段
                    if (fieldName.equalsIgnoreCase(name)) {
                        fieldMap.put(name, value);
                        break;
                    }
                }
            }
            // 添加这行的明细数据
            detailList.add(fieldMap);
        }
        return detailList;
    }

    /**
     * 获取当行明细数据相应字段的值
     * @param fieldName 字段名
     * @param c 列数组
     * @return 字段值
     */
    public static String getDetailData(String fieldName, Cell[] c) {
        for (Cell c1 : c) {
            String name = c1.getName();
            if (name.equals(fieldName)) {
                return Util.null2String(c1.getValue());
            }
        }
        return null;
    }

    /**
     * 获取下拉框字段的选项显示名称
     * @param tableName 流程表名
     * @param fieldName 下拉框字段数据库名称
     * @param selectValue 下拉框选项id（值）
     * @param recordSet recordSet
     * @return 下拉框字段的选项显示名称
     */
    public static String getSelectItemShowName(String tableName, String fieldName, int selectValue, RecordSet recordSet){
       return FieldUtil.getSelectItemShowName(tableName,null,fieldName,selectValue,recordSet);
    }

    /**
     * 获取下拉框字段的选项显示名称
     * @param tableName 流程表名
     * @param detailTableName 明细表名,如何是明细字段请填写
     * @param fieldName 下拉框字段数据库名称
     * @param selectValue 下拉框选项id（值）
     * @param recordSet recordSet
     * @return 下拉框字段的选项显示名称
     */
    public static String getSelectItemShowName(String tableName, String detailTableName,String fieldName,
                                               int selectValue, RecordSet recordSet){
        return FieldUtil.getSelectItemShowName(tableName, detailTableName, fieldName, selectValue, recordSet);
    }

    /**
     * 获取下拉框字段的选项显示名称
     * @param fieldId 下拉框字段id，id不需要加上“field”,只需要后面的数字
     * @param selectValue 下拉框选项id（值）
     * @param recordSet recordSet
     * @return 下拉框字段的选项显示名称
     */
    public static String getSelectItemShowName(int fieldId, int selectValue, RecordSet recordSet){
        return FieldUtil.getSelectItemShowName(fieldId, selectValue, recordSet);
    }

    /**
     * 获取公共选择框的选项显示名称
     * @param fieldId 字段id，对应 workflow_billfield 表的id
     * @param selectValue 字段值
     * @param recordSet recordSet
     * @return 公共选择框的选项显示名称
     */
    public static String getPublicSelectorShowName(int fieldId, int selectValue, RecordSet recordSet) {
        return FieldUtil.getPublicSelectorShowName(fieldId, selectValue, recordSet);
    }

    /**
     * 流程是否归档
     * @param requestId 请求id
     * @param recordSet recordSet
     * @return 如果归档返回true
     */
    public static boolean isWorkflowFinished(int requestId, RecordSet recordSet) {
        recordSet.executeQuery("select requestid from workflow_requestbase where requestid=? and currentnodetype='3'",requestId);
        return recordSet.next();
    }

    /**
     * 获取流程表单表名
     * @param workflowId 流程id
     * @param recordSet recordSet
     * @return 流程表单表名
     */
    public static String getWorkflowTableName(int workflowId,RecordSet recordSet) {
        recordSet.executeQuery("select b.tablename from workflow_base w,workflow_bill b where  " +
                "w.id = ? AND w.formid = b.id", workflowId);
        recordSet.next();
        return recordSet.getString("tablename");
    }

    /**
     * 根据请求id获取流程表单表名
     * @param requestId 请求id
     * @param recordSet recordSet
     * @return 流程表单表名
     */
    public static String getWorkflowTableNameByRequestId(int requestId, RecordSet recordSet) {
        recordSet.executeQuery("SELECT b.tablename from workflow_bill b " +
                "join workflow_base a on a.formid = b.id " +
                "join workflow_requestbase r on r.WORKFLOWID = a.id " +
                "where r.REQUESTID = ?", requestId);
        recordSet.next();
        return recordSet.getString("tablename");
    }

    /**
     * 获取流程的归档时间，格式为yyyy-MM-dd HH:mm:ss，如果流程没有归档则返回空字符串
     * @param requestId 请求id
     * @param recordSet recordSet
     * @return 流程的归档时间
     */
    public static String getWorkflowArchiveTime(int requestId, RecordSet recordSet) {
        recordSet.executeQuery("select lastoperatedate,lastoperatetime from workflow_requestbase " +
                "where requestid=? and currentnodetype='3'", requestId);
        if (!recordSet.next()) {
            return "";
        }
        return recordSet.getString("lastoperatedate") + " " + recordSet.getString("lastoperatetime");
    }

    /**
     * 获取流程请求名称
     * @param requestId 请求id
     * @param recordSet recordSet
     * @return 流程请求名称
     */
    public static String getRequestName(int requestId, RecordSet recordSet) {
        recordSet.executeQuery("SELECT requestname from workflow_requestbase WHERE REQUESTID = ?", requestId);
        recordSet.next();
        return recordSet.getString("requestname");
    }

    /**
     * 获取流程创建人id
     * @param requestId 请求id
     * @param recordSet recordSet
     * @return 流程创建人id
     */
    public static int getWorkflowCreatorId(int requestId, RecordSet recordSet) {
        recordSet.executeQuery("select creater from workflow_requestbase where requestid=?", requestId);
        recordSet.next();
        return recordSet.getInt("creater");
    }

    /**
     * 判断当前流程id是否属于某个流程id现在或者后面的版本
     * @param compareWorkflowId 对比的流程id，当前流程id要属于这个流程id的现在或后面的版本
     * @param currentWorkflowId 当前流程id
     * @return 当前流程id是否属于某个流程id的后面版本
     */
    public static boolean isCurrentOrAfterWorkflowVersion(int compareWorkflowId, int currentWorkflowId) {
        WorkflowVersion workflowVersion = new WorkflowVersion(currentWorkflowId+"");
        List<Map<String, String>> allVersionList = workflowVersion.getAllVersionList();
        for (Map<String, String> versionItem : allVersionList) {
            int workflowId = Integer.parseInt(versionItem.get("id"));
            if (compareWorkflowId == workflowId && workflowId <= currentWorkflowId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定流程版本的流程id后面版本的流程id，例如获取v1后面版本的流程id，将返回v2,v3,v4版本的流程id
     * @param versionWorkflowId 指定流程版本的流程id
     * @return 后面版本的流程id
     */
    public static List<Integer> getLatterVersionsWorkflowIds(int versionWorkflowId) {
        List<Integer> allVersionWorkflowIds = getAllVersionsWorkflowIds(versionWorkflowId);
        return allVersionWorkflowIds.stream().filter(workflowId -> workflowId > versionWorkflowId)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定流程版本的流程id前面版本的流程id，例如获取v4前面版本的流程id，将返回v3,v2,v1版本的流程id
     * @param versionWorkflowId 指定流程版本的流程id
     * @return 前面版本的流程id
     */
    public static List<Integer> getPreiousVersionsWorkflowIds(int versionWorkflowId) {
        List<Integer> allVersionWorkflowIds = getAllVersionsWorkflowIds(versionWorkflowId);
        return allVersionWorkflowIds.stream().filter(workflowId -> workflowId < versionWorkflowId)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定流程的所有流程版本的流程id
     * @param workflowId 指定流程id
     * @return 所有流程版本的流程id
     */
    public static List<Integer> getAllVersionsWorkflowIds(int workflowId) {
        List<Integer> result = new ArrayList<>();
        WorkflowVersion workflowVersion = new WorkflowVersion(workflowId+"");
        List<Map<String, String>> allVersionList = workflowVersion.getAllVersionList();
        for (Map<String, String> versionItem : allVersionList) {
            int wId = Integer.parseInt(versionItem.get("id"));
            result.add(wId);
        }
        return result;
    }

    public static int getWorkflowId(int requestId, RecordSet recordSet) {
        recordSet.executeQuery("select workflowid from workflow_requestbase where requestid=?", requestId);
        recordSet.next();
        return recordSet.getInt("workflowid");
    }

    /**
     * 根据表单id获取流程id，只获取生效版本的流程id
     * @param formId 表单id
     * @param recordSet recordSet
     * @return 流程id
     */
    public static Optional<Integer> getWorkflowIdByFormId(int formId,RecordSet recordSet) {
        recordSet.executeQuery("select id,activeversionid " +
                "from workflow_base where formid=?", formId);
        if (!recordSet.next()) {
            return Optional.empty();
        }
        if (!recordSet.getString("activeversionid").isEmpty()) {
            return Optional.of(recordSet.getInt("activeversionid"));
        }
        return Optional.of(recordSet.getInt("id"));
    }

    /**
     * 获取流程节点信息
     * @param workflowId 流程id
     * @param recordSet recordSet
     * @return 流程节点信息列表
     */
    public static List<WorkflowNodeInfo> getNodeList(int workflowId,RecordSet recordSet) {
        List<WorkflowNodeInfo> result = new ArrayList<>();
        recordSet.executeQuery("SELECT b.id,b.nodename,b.nodeattribute,n.nodetype " +
                "FROM workflow_flownode n JOIN workflow_nodebase b ON n.nodeid=b.id " +
                "WHERE n.workflowid=?", workflowId);
        while (recordSet.next()) {
            WorkflowNodeInfo nodeInfo = new WorkflowNodeInfo();
            nodeInfo.setId(recordSet.getInt("id"));
            nodeInfo.setNodeName(recordSet.getString("nodename"));
            nodeInfo.setAttribute(recordSet.getString("nodeattribute"));
            nodeInfo.setNodeType(recordSet.getString("nodetype"));
            result.add(nodeInfo);
        }
        return result;
    }

    /**
     * 获取流程创建节点信息
     * @param workflowId 流程id
     * @param recordSet recordSet
     * @return 流程创建节点信息
     */
    @Nullable
    public static WorkflowNodeInfo getCreateNode(int workflowId, RecordSet recordSet) {
        List<WorkflowNodeInfo> nodeList = getNodeList(workflowId, recordSet);
        for (WorkflowNodeInfo nodeInfo : nodeList) {
            if ("0".equals(nodeInfo.getNodeType())) {
                return nodeInfo;
            }
        }
        return null;
    }

    /**
     * 获取当前流程的节点操作id，为workflow_currentoperator表的id
     * @param receiveUserId 节点接收者id
     * @param nodeId 节点id
     * @param requestId 请求id
     * @param recordSet recordSet
     * @return 当前流程的节点操作id
     */
    public static Optional<Integer> getCurrentOperateId(int receiveUserId, int nodeId,
                                                        int requestId, RecordSet recordSet) {
        recordSet.executeQuery("SELECT id from workflow_currentoperator " +
                "where requestid=? and nodeid=? and userid=? and islasttimes=1 order by id desc",
                requestId,nodeId,receiveUserId);
        if (!recordSet.next()) {
            return Optional.empty();
        }
        return Optional.of(recordSet.getInt("id"));
    }

    /**
     * 获取指定已流转的节点的操作者id，如果没有找到则返回空列表
     * @param requestId 请求id
     * @param nodeId 节点id
     * @param isRemark 指定操作类型，只获取该操作类型的操作者,如果传空则不指定
     * @param recordSet recordSet
     * @return 节点的操作者id列表
     */
    public static List<Integer> getFlowedNodeOperatorIds(int requestId, int nodeId,@Nullable Integer isRemark,
                                                         RecordSet recordSet) {
        List<Integer> operatorIds = new ArrayList<>();
        String sql = "SELECT a.userid FROM workflow_currentoperator a " +
                "WHERE a.requestid=? AND a.nodeid=? AND a.islasttimes=1";
        if (isRemark != null) {
            sql += " AND a.isremark=" + isRemark;
        }
        recordSet.executeQuery(sql, requestId, nodeId);
        while (recordSet.next()) {
            operatorIds.add(recordSet.getInt("userid"));
        }
        return operatorIds;
    }

    /**
     * 获取流程当前节点操作者id，如果没有找到则返回空列表
     * @param requestId 请求id
     * @param isRemark 指定操作类型，只获取该操作类型的操作者，如果传空则不指定
     * @param recordSet recordSet
     * @return 流程当前节点操作者id集合
     */
    public static List<Integer> getCurrentNodeOperatorIds(int requestId, @Nullable Integer isRemark,
                                                          RecordSet recordSet) {
        List<Integer> operatorIds = new ArrayList<>();
        String sql = "SELECT a.userid FROM workflow_currentoperator a " +
                "JOIN workflow_requestbase r ON a.requestid=r.requestid AND a.nodeid=r.CURRENTNODEID " +
                "WHERE a.requestid=? AND a.islasttimes=1";
        if (isRemark != null) {
            sql += " AND a.isremark=" + isRemark;
        }
        recordSet.executeQuery(sql, requestId);
        while (recordSet.next()) {
            operatorIds.add(recordSet.getInt("userid"));
        }
        return operatorIds;
    }

    /**
     * 获取当前流程节点id，不适用于分叉等多个节点的情况
     * @param requestId 请求id
     * @param recordSet recordSet
     * @return 当前流程节点id
     */
    public static int getCurrentNodeId(int requestId, RecordSet recordSet) {
        recordSet.executeQuery("select currentnodeid from workflow_requestbase where requestid=?", requestId);
        recordSet.next();
        return recordSet.getInt("currentnodeid");
    }

    /**
     * 获取当前流程节点id，适用于分叉等多个节点的情况
     * @param requestId 请求id
     * @param recordSet recordSet
     * @return 前流程所处节点id集合
     */
    public static List<Integer> getCurrentNodeIds(int requestId, RecordSet recordSet) {
        List<Integer> result = new ArrayList<>();
        recordSet.executeQuery("SELECT nownodeid FROM workflow_nownode WHERE requestid = ?", requestId);
        while (recordSet.next()) {
            result.add(recordSet.getInt("nownodeid"));
        }
        return result;
    }

    /**
     * 是否是公文流程
     * @param workflowId 流程id
     * @param recordSet recordSet
     * @return 如果是公文流程返回true
     */
    public static boolean isOdcWorkflow(int workflowId, RecordSet recordSet) {
        recordSet.executeQuery("SELECT id from workflow_base WHERE isworkflowdoc=1 AND id=? " +
                "AND officaltype>0", workflowId);
        return recordSet.next();
    }

    public static String getWorkflowName(int workflowId, RecordSet recordSet) {
        recordSet.executeQuery("select workflowname from workflow_base where id=?", workflowId);
        recordSet.next();
        return recordSet.getString("workflowname");
    }

    /**
     * 获取流程附件存放知识目录id
     * @param workflowId 流程id
     * @param recordSet recordSet
     * @return Optional 知识目录id
     */
    public static Optional<Integer> getAttachmentSaveDirId(int workflowId,RecordSet recordSet) {
        recordSet.executeQuery("select doccategory from workflow_base where id=?",
                workflowId);
        recordSet.next();
        String doccategory = recordSet.getString("doccategory");
        if (doccategory.contains(",")) {
            String[] split = doccategory.split(",");
            String last = split[split.length - 1];
            if (last.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(last));
        }
        if (doccategory.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(doccategory));
    }

    private static String getTableFieldValue(int requestId, String fieldName,String tableName, RecordSet recordSet) {
        if (!recordSet.executeQuery("select " + fieldName + " from " + tableName + " where requestid=?", requestId)) {
            log.error("查询流程字段值出错，sql错误");
            return "";
        }
        recordSet.next();
        return recordSet.getString(fieldName);
    }

}
