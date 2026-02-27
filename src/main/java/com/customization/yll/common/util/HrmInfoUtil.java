package com.customization.yll.common.util;

import cn.hutool.core.convert.Convert;
import com.customization.yll.common.enu.LanguageType;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import weaver.conn.RecordSet;

import java.util.Optional;

/**
 * 人力资源工具类
 *
 * @author yaolilin
 */
@UtilityClass
public class HrmInfoUtil {

    public static String getWorkCode(Integer userId, RecordSet recordSet) {
        recordSet.executeQuery("select workcode from hrmresource where id =?", userId);
        recordSet.next();
        return recordSet.getString("workcode");
    }

    public Optional<Integer> getUserIdByWorkCode(String workCode, RecordSet recordSet) {
        recordSet.executeQuery("select id from hrmresource where workcode=?", workCode);
        if (!recordSet.next()) {
            return Optional.empty();
        }
        return Optional.of(recordSet.getInt("id"));
    }

    /**
     * 获取人力资源姓名（中文），如果人力资源姓名中使用多语言文本，将获取中文文本
     *
     * @param userId    用户id
     * @param recordSet recordSet
     * @return 人力资源姓名（中文）
     */
    public static String getLastNameCn(Integer userId, RecordSet recordSet) {
        String lastName = getLastName(userId, recordSet);
        return MultiLanguageUtil.analyzeMultiLanguageText(lastName, LanguageType.CN, recordSet);
    }

    public static String getLastName(Integer userId, RecordSet recordSet) {
        if (userId == null) {
            return "";
        }
        recordSet.executeQuery("select lastname from hrmresource where id =?", userId);
        recordSet.next();
        return recordSet.getString("lastname");
    }

    public static String getPostName(Integer userId, RecordSet recordSet) {
        recordSet.executeQuery("SELECT j.jobtitlename from hrmjobtitles j,hrmresource h WHERE h.id =? AND j.id = h.jobtitle"
                , userId);
        recordSet.next();
        return recordSet.getString("jobtitlename");
    }

    /**
     * 获取部门全路径，如：研发部/测试部
     *
     * @param departmentId 部门id 起始部门id，根据此部门id一直向上获取父部门，生成部门全路径
     * @param splitPatten  部门路径分隔符
     * @param recordSet    RecordSet
     * @return 部门全路径，如果获取不到则返回空字符串
     */
    @NotNull
    public String getFullDepartmentPath(int departmentId, String splitPatten, RecordSet recordSet) {
        StringBuilder path = new StringBuilder(getDepartmentName(departmentId, recordSet));
        if (path.length() == 0) {
            return "";
        }
        Integer parentId = getParentDepartmentId(departmentId, recordSet);
        while (parentId != null) {
            String parentDepName = getDepartmentName(parentId, recordSet);
            path.insert(0, parentDepName + splitPatten);
            parentId = getParentDepartmentId(parentId, recordSet);
        }
        // 如果开头有分隔符，则删除
        if (path.indexOf(splitPatten) == 0) {
            path.delete(0, splitPatten.length());
        }
        return path.toString();
    }

    public static String getDepartmentName(int departmentId, RecordSet recordSet) {
        recordSet.executeQuery("select departmentname from hrmdepartment where id=?", departmentId);
        recordSet.next();
        return recordSet.getString("departmentname");
    }

    /**
     * 获取部门上级部门id
     *
     * @param departmentId 部门id
     * @param recordSet    RecordSet
     * @return 上级部门id, 如果获取不到则返回 null
     */
    @Nullable
    public Integer getParentDepartmentId(int departmentId, RecordSet recordSet) {
        String sql = "SELECT supdepid FROM hrmdepartment WHERE id=?";
        recordSet.executeQuery(sql, departmentId);
        recordSet.next();
        return Convert.toInt(recordSet.getString("supdepid"));
    }

    public static String getSubCompanyName(int subCompanyId, RecordSet recordSet) {
        recordSet.executeQuery("select subcompanyname from hrmsubcompany where id=?", subCompanyId);
        recordSet.next();
        return recordSet.getString("subcompanyname");
    }

    public static String getUserDepartmentName(int userId, RecordSet recordSet) {
        recordSet.executeQuery("select d.departmentname from hrmresource r,hrmdepartment d " +
                "where r.id=? and r.departmentid=d.id", userId);
        recordSet.next();
        return recordSet.getString("departmentname");
    }

    /**
     * 获取用户部门id
     *
     * @param userId    用户id
     * @param recordSet RecordSet
     * @return 部门id，如果获取不到则返回 null
     */
    @Nullable
    public static Integer getUserDepartmentId(int userId, RecordSet recordSet) {
        recordSet.executeQuery("select d.id from hrmresource r,hrmdepartment d " +
                "where r.id=? and r.departmentid=d.id", userId);
        recordSet.next();
        return Convert.toInt(recordSet.getString("id"));
    }

    public static String getUserSubCompanyName(int userId, RecordSet recordSet) {
        recordSet.executeQuery("select c.subcompanyname from hrmresource r,hrmsubcompany c " +
                "where r.id=? and r.subcompanyid1=c.id", userId);
        recordSet.next();
        return recordSet.getString("subcompanyname");
    }

    public static String getPhoneNumber(int userId, RecordSet recordSet) {
        recordSet.executeQuery("select mobile from hrmresource where id=?", userId);
        recordSet.next();
        return recordSet.getString("mobile");
    }

    public static String getLoginId(int userId, RecordSet recordSet) {
        recordSet.executeQuery("select loginid from hrmresource where id=?", userId);
        recordSet.next();
        return recordSet.getString("loginid");
    }
}
