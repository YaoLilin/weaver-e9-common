package com.customization.yll.common.workflow.constants;

/**
 * @author 姚礼林
 * @desc 流程流转意见日志类型
 * @date 2025/5/9
 **/
public enum WorkflowLogType {
    /**
     * 批准
     */
    APPROVE("0"),
    /**
     * 保存
     */
    SAVE("1"),
    /**
     * 提交
     */
    SUBMIT("2"),
    /**
     * 退回
     */
    REJECT("3"),
    /**
     * 重新打开
     */
    REOPEN("4"),
    /**
     * 删除
     */
    DELETE("5"),
    /**
     * 激活
     */
    ACTIVATE("6"),
    /**
     * 转发
     */
    FORWARD("7"),
    /**
     * 批注
     */
    COMMENT("9"),
    /**
     * 意见征询
     */
    OPINION_CONSULT("a"),
    /**
     * 意见征询回复
     */
    OPINION_CONSULT_REPLY("b"),
    /**
     * 强制归档
     */
    FORCED_ARCHIVE("e"),
    /**
     * 转办
     */
    FORWARD_HANDLE("h"),
    /**
     * 干预
     */
    INTERVENE("i"),
    /**
     * 转办反馈
     */
    FORWARD_HANDLE_FEEDBACK("j"),
    /**
     * 督办
     */
    SUPERVISE_HANDLE("s"),
    /**
     * 抄送
     */
    COPY("t");

    private final String value;

    WorkflowLogType(String value) {
        this.value = value;
    }

    public String  getValue() {
        return value;
    }
}
