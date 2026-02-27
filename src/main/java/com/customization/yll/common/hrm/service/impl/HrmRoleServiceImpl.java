package com.customization.yll.common.hrm.service.impl;

import com.customization.yll.common.exception.SqlExecuteException;
import com.customization.yll.common.hrm.service.HrmRoleService;
import weaver.conn.RecordSet;

/**
 * @author 姚礼林
 * @desc 人力资源角色业务类
 * @date 2025/12/30
 **/
public class HrmRoleServiceImpl implements HrmRoleService {
    private final RecordSet recordSet;

    public HrmRoleServiceImpl(RecordSet recordSet) {
        this.recordSet = recordSet;
    }

    /**
     * 该用户是否属于某一角色
     *
     * @param userId 用户id
     * @param roleId 角色id
     * @throws SqlExecuteException      如果 sql 执行失败则抛出此异常
     * @throws IllegalArgumentException 如果参数错误则抛出此异常，userId 和 roleId 需要大于 0
     */
    @Override
    public boolean isUserInRole(int userId, int roleId) throws SqlExecuteException {
        if (userId < 1) {
            throw new IllegalArgumentException("[userId] 参数不正确，请传入大于0的数字");
        }
        if (roleId < 1) {
            throw new IllegalArgumentException("[roleId] 角色id参数不正确，请传入大于0的数字");
        }
        if (!recordSet.executeQuery("SELECT id FROM hrmrolemembers WHERE roleid=? AND resourceid=?",
                roleId, userId)) {
            throw new SqlExecuteException("查询人力资源角色成员表出错");
        }
        return recordSet.next();
    }
}
