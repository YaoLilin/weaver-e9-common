package com.customization.yll.common.bean;

/**
 * @author yaolilin
 * @desc 流程节点信息
 * @date 2025/2/28
 **/
public class WorkflowNodeInfo {
    private Integer id;
    private String nodeName;
    /**
     * 节点属性，0：一般，1：分叉起始点，2：分叉中间点，3：通过分支数合并，4：指定通过分支合并
     */
    private String attribute;
    /**
     * 节点类型，0：创建 1：审批 2：提交 3：归档 5：等待 6：自动处理
     */
    private String nodeType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
