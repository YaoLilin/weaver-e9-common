package com.customization.yll.common.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.bean.FieldParamMap;
import com.customization.yll.common.enu.FieldType;
import com.customization.yll.common.exception.FieldValueEmptyException;
import com.customization.yll.common.util.SqlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 姚礼林
 * @desc 数据库表字段映射到接口字段
 * @date 2025/7/21
 **/
public class TableFieldApiParamMapManager {
    private final  RecordSet recordSet;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public TableFieldApiParamMapManager(RecordSet recordSet) {
        this.recordSet = recordSet;
    }

    public TableFieldApiParamMapManager() {
        recordSet = new RecordSet();
    }

    /**
     * 生成接口请求体json参数
     * @param mapConfigs 接口参数映射配置，配置表字段和接口参数的映射关系
     * @param dbTable 数据来源表名
     * @param sqlCondition sql 条件,不带 WHERE
     * @return 请求体json参数
     * @throws FieldValueEmptyException 字段校验为空异常，如果字段为空并且参数配置为必填，则抛出此异常
     */
    public List<Map<String, Object>> getApiParams(List<FieldParamMap> mapConfigs, String dbTable,
                                                  @Nullable String sqlCondition) throws FieldValueEmptyException{
        if (CollUtil.isEmpty(mapConfigs) || StrUtil.isBlank(dbTable)) {
            return Collections.emptyList();
        }
        String querySql = getQuerySql(mapConfigs, dbTable, sqlCondition);
        if (!recordSet.executeQuery(querySql)) {
            log.error("sql 查询失败，sql:" + querySql);
            return Collections.emptyList();
        }
        return getApiParams(recordSet, mapConfigs);
    }

    /**
     * 生成接口请求体json参数
     * @param queriedRs 已经执行查询的RecordSet对象，将使用此RecordSet获取查询数据，可使用 {@link #buildQuerySql}生成查询sql。
     * @param mapConfigs 接口参数映射配置
     * @return 请求体json参数
     * @throws FieldValueEmptyException 字段校验为空异常，如果字段为空并且参数配置为必填，则抛出此异常
     */
    public List<Map<String, Object>> getApiParams(RecordSet queriedRs,List<FieldParamMap> mapConfigs)
            throws FieldValueEmptyException{
        List<Map<String, Object>> result = new ArrayList<>();
        while (queriedRs.next()) {
            Map<String, Object> param = new HashMap<>(10);
            mapConfigs.forEach(i ->{
                Object value = queriedRs.getString(i.getValueFieldName());
                if (i.getSkipCondition() != null && i.getSkipCondition().test((String) value)) {
                    return;
                }
                value = getParamValue(i, value, queriedRs);
                param.put(i.getParamName(), value);
            });
            result.add(param);
        }
        return result;
    }

    /**
     * 构建查询sql
     * @param mapConfigs 接口参数映射配置
     * @param dbTable 数据来源表名
     * @param tableAlias 数据表别名
     * @return 查询sql
     */
    public String buildQuerySql(List<FieldParamMap> mapConfigs, String dbTable, @Nullable String tableAlias) {
        List<String> fieldNames = mapConfigs.stream()
                .map(FieldParamMap::getValueFieldName).collect(Collectors.toList());
        return SqlUtil.buildQuerySql(fieldNames, dbTable, tableAlias);
    }

    /**
     * 根据传入的字段名构建查询sql
     * @param fieldNames 查询字段集合
     * @param dbTable 数据表名
     * @param tableAlias 数据表别名
     * @return 查询sql
     */
    public String buildQuerySqlByFieldNames(List<String> fieldNames, String dbTable, @Nullable String tableAlias) {
        return SqlUtil.buildQuerySql(fieldNames, dbTable, tableAlias);
    }

    private Object getParamValue(FieldParamMap i, Object value , RecordSet recordSet) {
        if (i.isNotEmpty() && StrUtil.isEmpty((String) value)) {
            throw new FieldValueEmptyException(String.format("[%s] 字段为空", i.getValueFieldName()),
                    i.getValueFieldName());
        }
        if (i.getConvertFunction() != null) {
            value =  i.getConvertFunction().apply((String) value);
        }
        if (i.getFieldType() != null) {
            value = convertFieldType(String.valueOf(value), i.getFieldType());
        }
        return value;
    }

    @NotNull
    private String getQuerySql(List<FieldParamMap> mapConfigs, String dbTable, @Nullable String sqlCondition) {
        String querySql = buildQuerySql(mapConfigs, dbTable, null);
        if (StrUtil.isNotBlank(sqlCondition)) {
            querySql += " WHERE " + sqlCondition;
        }
        return querySql;
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
