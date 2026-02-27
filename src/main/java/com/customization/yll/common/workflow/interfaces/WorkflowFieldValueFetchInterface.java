package com.customization.yll.common.workflow.interfaces;

import com.customization.yll.common.workflow.constants.GetWorkflowFieldDataWay;
import com.customization.yll.common.workflow.constants.SystemParam;

import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 获取流程字段值接口
 * @date 2025/10/29
 **/
public interface WorkflowFieldValueFetchInterface {

    /**
     * 获取流程主表字段值，如果没有指定 {@link GetWorkflowFieldDataWay} 则直接获取表单字段值，不进行转换
     *
     * @param requestId               流程请求id
     * @param fieldId                 字段id，不需要带上"filed"
     * @param getWorkflowFieldDataWay 字段取值方式，比如如果字段是下拉框，可以取下拉框的显示名，如果是人力资源字段，可以获取人力
     *                                资源字段的显示名
     * @return 字段值
     */
    String getFieldValueByFieldId(int requestId, int fieldId, GetWorkflowFieldDataWay getWorkflowFieldDataWay);

    /**
     * 根据字段id获取流程主表字段值
     *
     * @param requestId 流程请求id
     * @param fieldId   字段id，不需要带上"filed"
     * @return 字段值
     */
    String getFieldValueByFieldId(int requestId, int fieldId);

    /**
     * 根据字段名获取流程主表字段值
     *
     * @param requestId 流程请求id
     * @param fieldName 字段数据库名
     * @return 字段值，如果获取不到则返回空字符串
     */
    String getFieldValueByFieldName(int requestId, String fieldName);

    /**
     * 获取多个流程主表字段值，如果获取不到则返回空 map
     *
     * @param requestId  流程请求id
     * @param fieldNames 需要获取字段值的字段名集合
     * @return 字段名对应的字段值 map，如果获取不到则返回空 map
     */
    Map<String, String> getFieldValueByFieldNames(int requestId, List<String> fieldNames);

    /**
     * 获取流程指定明细的多个字段的值
     *
     * @param requestId  流程请求id
     * @param detailNum  流程明细表序号，必需大于0，例如明细表1的序号为1
     * @param fieldNames 需要获取字段值的字段名集合
     * @return 明细字段的值
     */
    List<Map<String, String>> getDetailFields(int requestId, int detailNum, List<String> fieldNames);

    /**
     * 获取流程的系统字段值，比如标题，紧急程度等
     *
     * @param systemParam 系统字段
     * @return 系统字段值
     */
    String getSystemFieldValue(SystemParam systemParam);
}
