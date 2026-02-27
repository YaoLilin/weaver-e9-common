package com.customization.yll.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import lombok.Data;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc sql 工具类
 * @date 2023/9/5
 */
@UtilityClass
public class SqlUtil {

    /**
     * 生成查询sql
     * @param fieldNames 查询字段名集合
     * @param tableName 表名
     * @return 查询sql
     */
    @NotNull
    public static String buildQuerySql(List<String> fieldNames, String tableName) {
        return buildQuerySql(fieldNames, tableName, null);
    }

    /**
     * 生成查询sql
     * @param fieldNames 查询字段名集合
     * @param tableName 表名
     * @param tableAlias 表别名
     * @return 查询sql
     */
    @NotNull
    public static String buildQuerySql(List<String> fieldNames, String tableName, @Nullable String tableAlias) {
        if (CollUtil.isEmpty(fieldNames) || StrUtil.isBlank(tableName)) {
            return "";
        }
        StringBuilder sql = new StringBuilder("SELECT ");
        fieldNames.forEach(i -> {
            if (StrUtil.isNotBlank(tableAlias)) {
                sql.append(tableAlias).append(".").append(i).append(",");
            }else {
                sql.append(i).append(",");
            }
        });
        sql.delete(sql.length() - 1, sql.length());
        sql.append(" FROM ").append(tableName);
        if (StrUtil.isNotBlank(tableAlias)) {
            sql.append(" ").append(tableAlias);
        }
        return sql.toString();
    }

    /**
     * 生成sql更新语句的set部分，例如 name=?,age=?，语句里使用了占位符，执行时请使用参数进行替换
     *
     * @param fieldNames 字段名
     * @return sql set语句
     */
    public static String buildUpdateSql(List<String> fieldNames) {
        StringBuilder sql = new StringBuilder();
        fieldNames.forEach(i -> sql.append(i).append("=?").append(","));
        sql.delete(sql.length() - 1, sql.length());
        return sql.toString();
    }

    /**
     * 生成sql更新语句，不包括where部分，例如 update table_name1 set name=?,age=?，语句里使用了占位符，执行时请使用参数进行替换
     *
     * @param tableName  更新表名
     * @param fieldNames 字段名
     * @return sql set语句
     */
    public static String buildUpdateSql(String tableName, List<String> fieldNames) {
        return "update " + tableName + " set " + buildUpdateSql(fieldNames);
    }

    /**
     * 生成sql插入语句,语句中字段的值用?占位符表示
     *
     * @param fieldNames 字段名
     * @return sql 插入语句
     */
    public static String buildInsertSql(String tableName, List<String> fieldNames) {
        StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
        fieldNames.forEach(i -> sql.append(i).append(","));
        sql.delete(sql.length() - 1, sql.length());
        sql.append(") values(");
        for (int i = 0; i < fieldNames.size(); i++) {
            sql.append("?,");
        }
        sql.delete(sql.length() - 1, sql.length());
        sql.append(")");
        return sql.toString();
    }

    /**
     * 构件相等条件的where语句<br>
     * 如果没有条件语句，泽返回空串，表示没有条件
     *
     * @param conditions  条件，key为字段名，value为字段值
     * @param paramValues sql 占位符对应的值，当生成条件语句时会将条件中的值插入到此列表中
     * @return 带where关键字的SQL部分
     */
    public static String buildEqualsWhere(Map<String, Object> conditions, List<Object> paramValues) {
        if (CollUtil.isEmpty(conditions)) {
            return StrUtil.EMPTY;
        }
        Entity entity = Entity.create();
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            entity.set(entry.getKey(), entry.getValue());
        }
        final StringBuilder sb = new StringBuilder(" WHERE ");
        boolean isNotFirst = false;
        for (Map.Entry<String, Object> entry : entity.entrySet()) {
            if (isNotFirst) {
                sb.append(" and ");
            } else {
                isNotFirst = true;
            }
            sb.append(entry.getKey()).append(" = ?");
            paramValues.add(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * 构建 SQL IN 子句的占位符字符串
     *
     * @param size 参数数量
     * @return 占位符字符串，如 "?,?,?"
     */
    @NotNull
    public static String buildInClausePlaceholders(int size) {
        if (size <= 0) {
            return "";
        }
        StringBuilder placeholders = new StringBuilder(size * 2 - 1);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }
        return placeholders.toString();
    }

    /**
     * 构建分页 SQL
     *
     * @param dbType   数据库类型
     * @param baseSql  基础查询 SQL（不包含 order by / limit）
     * @param orderBy  排序 SQL（可传入 "order by xxx" 或 "xxx"）
     * @param offset   偏移量
     * @param limit    每页数量
     * @return 分页 SQL 与参数
     */
    public static SqlPageResult buildPageSql(String dbType, String baseSql, String orderBy, int offset, int limit) {
        String normalizedDbType = normalizeDbType(dbType);
        String orderBySql = normalizeOrderBy(orderBy);
        List<Object> params = new ArrayList<>(2);
        String sql;
        if ("mysql".equals(normalizedDbType) || "mariadb".equals(normalizedDbType)) {
            sql = baseSql + orderBySql + "limit ?, ?";
            params.add(offset);
            params.add(limit);
        } else if ("postgresql".equals(normalizedDbType) || "postgres".equals(normalizedDbType)) {
            sql = baseSql + orderBySql + "limit ? offset ?";
            params.add(limit);
            params.add(offset);
        } else if ("oracle".equals(normalizedDbType)) {
            sql = "select * from (select t.*, rownum rn from (" + baseSql + orderBySql
                + ") t where rownum <= ?) where rn >= ?";
            params.add(offset + limit);
            params.add(offset + 1);
        } else {
            String windowOrderBy = normalizeOrderByForWindow(orderBySql);
            sql = "select * from (select t.*, ROW_NUMBER() OVER (" + windowOrderBy + ") as rn from ("
                + baseSql + ") t) tt where tt.rn between ? and ?";
            params.add(offset + 1);
            params.add(offset + limit);
        }
        return new SqlPageResult(sql, params);
    }

    private static String normalizeDbType(String dbType) {
        if (dbType == null) {
            return "";
        }
        return dbType.trim().toLowerCase();
    }

    private static String normalizeOrderBy(String orderBy) {
        if (StrUtil.isBlank(orderBy)) {
            return " ";
        }
        String trimmed = orderBy.trim();
        if (trimmed.toLowerCase().startsWith("order by")) {
            return " " + trimmed + " ";
        }
        return " order by " + trimmed + " ";
    }

    private static String normalizeOrderByForWindow(String orderBySql) {
        String trimmed = orderBySql == null ? "" : orderBySql.trim();
        if (StrUtil.isBlank(trimmed)) {
            return "order by (select 0)";
        }
        return trimmed;
    }

    @Data
    public static class SqlPageResult {
        private final String sql;
        private final List<Object> params;
    }
}
