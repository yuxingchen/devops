package com.devops.service;

import com.devops.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService {
    /**
     * 获取所有角色
     *
     * @return 角色列表
     */ 
    List<SysRole> list();
    /**
     * 创建角色
     *
     * @param role 角色信息
     */
    void createRole(SysRole role);
    /**
     * 修改角色
     *
     * @param role 角色信息
     */
    void updateRole(SysRole role);
    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);
} 