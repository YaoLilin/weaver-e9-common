package com.customization.yll.common.manager;

import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.bean.MapInfo;
import com.customization.yll.common.enu.FieldType;
import com.customization.yll.common.exception.FieldNotFoundException;
import com.customization.yll.common.exception.FieldValueEmptyException;
import lombok.Getter;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.Row;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author 姚礼林
 * @desc 流程字段映射，可以用于将流程字段映射到接口字段，可进行字段值的转换
 * 用法：首先调用 {@link #addMainFieldMapConfig} 方法添加主表字段映射配置，然后调用 {@link #mapMainField} 方法获取主表字段映射的接口参数
 * 对于明细表，调用 {@Link #addDetailFieldMap} 方法添加明细表字段映射配置，然后调用 {@link #mapDetailField} 方法获取明细表字段映射的接口参数
 * 调用 {@link #mapMainField} 或 {@link #mapDetailField} 后，如果要再次添加字段映射，记得调用
 * {@link #cleanMainFieldParamMap()} 或 {@link #cleanDetailFieldParamMap()} 方法清空之前的映射配置
 * @date 2024/7/10
 */
@Getter
public class WorkflowFieldMapper {
    private final Map<String ,List<MapInfo>> mainFieldMapConfig;
    private final Map<String ,List<MapInfo>> detailFieldMapConfig;

    public WorkflowFieldMapper() {
        mainFieldMapConfig = new HashMap<>(20);
        detailFieldMapConfig = new HashMap<>(20);
    }

    /**
     * 添加主表字段映射
     * @param fieldName 流程字段数据库名称，不区分大小写
     * @param mapInfo 映射配置
     */
    public void addMainFieldMapConfig(String fieldName, MapInfo mapInfo) {
        mainFieldMapConfig.computeIfAbsent(fieldName.toLowerCase(), k -> new ArrayList<>()).add(mapInfo);
    }

    /**
     * 添加明细表字段映射
     * @param fieldName 流程字段数据库名称，不区分大小写
     * @param mapInfo 映射配置
     */
    public void addDetailFieldMapConfig(String fieldName, MapInfo mapInfo) {
        detailFieldMapConfig.computeIfAbsent(fieldName.toLowerCase(), k -> new ArrayList<>()).add(mapInfo);
    }

    /**
     * 将会根据所配置的主表字段映射关系，映射到所配置的名称中，例如字段名称是 name,值为 John,配置的名称为 lastName,那么将会映射为 lastName = John
     * 如果配置了值转换，将会对主表字段的值进行转换，否则将会直接取字段值
     * @param mainFields 流程主表字段
     * @throws FieldValueEmptyException 如果配置了字段为非空，但流程字段值为空，将会抛出此异常
     * @return 映射结果
     */
    public Map<String, Object> mapMainField(Property[] mainFields)
            throws FieldValueEmptyException,FieldNotFoundException {
        verifyFormMainFieldExist(mainFields);
        Map<String, Object> params = new HashMap<>(20);
        for (Property property : mainFields) {
            putParamValue(property.getName(), property.getValue(), mainFieldMapConfig, params);
        }
        return params;
    }

    /**
     * 将会根据所配置的明细表字段映射关系，映射到所配置的名称中，例如字段名称是 name,值为 John,配置的名称为 lastName,那么将会映射为 lastName = John
     * 如果配置了值转换，将会对字段的值进行转换，否则将会直接取字段值
     * @param detailRows 明细行
     * @throws FieldValueEmptyException 如果配置了字段为非空，但流程字段值为空，将会抛出此异常
     * @return 映射结果
     */
    public List<Map<String, Object>> mapDetailField(Row[] detailRows)
            throws FieldValueEmptyException,FieldNotFoundException {
        List<Map<String, Object>> params = new ArrayList<>();
        if (detailRows.length > 0) {
            verifyFormDetailFieldExist(detailRows[0].getCell());
        }
        for (Row row : detailRows) {
            Map<String, Object> paramItem = new HashMap<>(20);
            for (Cell cell : row.getCell()) {
                putParamValue(cell.getName(), cell.getValue(), detailFieldMapConfig,paramItem);
            }
            if (!paramItem.isEmpty()) {
                params.add(paramItem);
            }
        }
        return params;
    }

    /**
     * 清空主表字段映射配置
     */
    public void cleanMainFieldParamMap() {
        mainFieldMapConfig.clear();
    }

    /**
     * 清空明细表字段映射配置
     */
    public void cleanDetailFieldParamMap() {
        detailFieldMapConfig.clear();
    }

    private void verifyFormMainFieldExist(Property[] mainFields) {
        verifyFormFieldExist(mainFieldMapConfig,
                fieldName -> Arrays.stream(mainFields).noneMatch(p -> p.getName().equalsIgnoreCase(fieldName)));
    }

    private void verifyFormDetailFieldExist(Cell[] detailFields) {
        verifyFormFieldExist(detailFieldMapConfig,
                fieldName -> Arrays.stream(detailFields).noneMatch(p -> p.getName().equalsIgnoreCase(fieldName)));
    }

    /**
     * 校验表单是否存在指定字段
     * @param fieldMap 表单字段与接口字段的映射
     * @param verifyFunc 校验条件，如果返回true则会抛出字段不存在的异常
     */
    private void verifyFormFieldExist(Map<String ,List<MapInfo>> fieldMap, Predicate<String> verifyFunc) {
        for (Map.Entry<String, List<MapInfo>> entry : fieldMap.entrySet()) {
            String fieldName = entry.getKey();
            if (verifyFunc.test(fieldName)) {
                throw new FieldNotFoundException(String.format("[%s] 字段在表单中未找到",fieldName),fieldName);
            }
        }
    }

    private void putParamValue(String fieldName, String fieldValue,Map<String ,List<MapInfo>> mapConfig,
                               Map<String, Object> params) {
        fieldName = fieldName.toLowerCase();
        List<MapInfo> mapInfoList = mapConfig.get(fieldName);
        if (mapInfoList == null) {
            return;
        }
        // 允许一个流程字段映射到多个接口字段
        for (MapInfo item : mapInfoList) {
            if (item.getSkipCondition() != null && item.getSkipCondition().test(fieldValue)) {
                    continue;
            }
            params.put(item.getParamName(), getParamValue(item, fieldName, fieldValue));
        }
    }



    private Object getParamValue(MapInfo mapInfo,String fieldName,String fieldValue) {
        if (mapInfo.isNotEmpty() && StrUtil.isEmpty(fieldValue)) {
            throw new FieldValueEmptyException(String.format("[%s] 字段为空", fieldName),fieldName);
        }
        if (mapInfo.getConvertFunction() != null) {
            return mapInfo.getConvertFunction().apply(fieldValue);
        } else if (mapInfo.getFieldType() != null) {
            return convertFieldType(fieldValue, mapInfo.getFieldType());
        } else {
            return fieldValue;
        }
    }

    private Object convertFieldType(String fieldValue, FieldType fieldType) {
        switch (fieldType) {
            case INTEGER:
                if (fieldValue == null || fieldValue.isEmpty()) {
                    return 0;
                }
                return Integer.parseInt(fieldValue);
            case DOUBLE:
                if (fieldValue == null || fieldValue.isEmpty()) {
                    return 0.0;
                }
                return Double.parseDouble(fieldValue);
            default:
                return fieldValue;
        }
    }


}
