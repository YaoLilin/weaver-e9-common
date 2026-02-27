package com.customization.yll.common.hrm.service;

/**
 * @author 姚礼林
 * @desc 人力资源角色业务类接口
 * @date 2025/12/30
 **/
public interface HrmRoleService {

    /**
     * 该用户是否属于某一角色
     * @param userId 用户id
     * @param roleId 角色id
     */
    boolean isUserInRole(int userId, int roleId);
}
