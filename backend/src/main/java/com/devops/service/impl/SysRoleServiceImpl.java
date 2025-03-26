package com.devops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.common.BusinessException;
import com.devops.entity.SysRole;
import com.devops.entity.SysUserRole;
import com.devops.mapper.SysRoleMapper;
import com.devops.mapper.SysUserRoleMapper;
import com.devops.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */ 
    @Override
    public List<SysRole> list() {
        return sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getDeleted, 0)
                        .orderByDesc(SysRole::getCreatedTime)
        );
    }

    /**
     * 创建角色
     *
     * @param role 角色
     */
    @Override
    public void createRole(SysRole role) {
        // 检查角色编码是否已存在
        if (sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getRoleCode, role.getRoleCode())
                        .eq(SysRole::getDeleted, 0)
        ) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        role.setDeleted(0);
        sysRoleMapper.insert(role);
    }

    /**
     * 更新角色
     *
     * @param role 角色
     */
    @Override
    public void updateRole(SysRole role) {
        // 检查角色是否存在
        SysRole dbRole = sysRoleMapper.selectById(role.getId());
        if (dbRole == null || dbRole.getDeleted() == 1) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色编码是否重复
        if (!dbRole.getRoleCode().equals(role.getRoleCode())) {
            if (sysRoleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getRoleCode, role.getRoleCode())
                    .eq(SysRole::getDeleted, 0)) > 0) {
                throw new BusinessException("角色编码已存在");
            }
        }

        sysRoleMapper.updateById(role);
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @Override
    public void deleteRole(Long id) {
        // 检查角色是否存在
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色是否被用户使用
        if (sysUserRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id)) > 0) {
            throw new BusinessException("角色已被用户使用，无法删除");
        }

        // 软删除角色
        SysRole updateRole = new SysRole();
        updateRole.setId(id);
        updateRole.setDeleted(1);
        sysRoleMapper.updateById(updateRole);
    }
} 