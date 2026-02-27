package com.engine.interfaces.yll.common.service;

import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.util.WeaTableTools;
import com.engine.core.impl.Service;
import com.weaverboot.weaComponent.impl.weaTable.table.impl.DefaultWeaTable;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author 姚礼林
 * @desc 获取列表sql
 * @date 2025/4/24
 **/
public class ListSqlQueryService extends Service {

    public String getListSqlBase64(String sessionKey) throws Exception {
        String sql = getListSql(sessionKey);
        return Base64.getEncoder().encodeToString(sql.getBytes(StandardCharsets.UTF_8));
    }

    @NotNull
    public String getListSql(String sessionKey) throws Exception {
        DefaultWeaTable table = WeaTableTools.checkTableStringConfig(sessionKey, DefaultWeaTable.class);
        String sqlWhere = table.getSqlwhere();
        String sqlForm = table.getSqlform();
        String backFields = table.getBackfields();
        String sqlGroupBy = table.getSqlgroupby();
        String sqlOrderBy = table.getSqlorderby();
        String sql = "select " + backFields + sqlForm;
        if (StrUtil.isNotBlank(sqlWhere)) {
            sql += " where "+ sqlWhere;
        }
        if (StrUtil.isNotBlank(sqlGroupBy)) {
            sql += " group by "+ sqlGroupBy;
        }
        if (StrUtil.isNotBlank(sqlOrderBy)) {
            sql += " order by "+ sqlOrderBy;
        }
        return sql;
    }
}
