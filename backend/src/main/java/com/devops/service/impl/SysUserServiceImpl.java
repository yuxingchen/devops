package com.devops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.common.BusinessException;
import com.devops.entity.SysRole;
import com.devops.entity.SysUser;
import com.devops.entity.SysUserRole;
import com.devops.mapper.SysRoleMapper;
import com.devops.mapper.SysUserMapper;
import com.devops.mapper.SysUserRoleMapper;
import com.devops.security.JwtUtils;
import com.devops.service.SysUserService;
import com.devops.util.UserContextUtil;
import com.devops.vo.UpdatePasswordVO;
import com.devops.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author yux
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 登录
     *
     * @param user 用户
     * @return JWT token
     */
    @Override
    public String login(SysUser user) {
        // 根据用户名查询用户
        SysUser dbUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, user.getUsername()).eq(SysUser::getDeleted, 0));
        if (dbUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验密码
        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (dbUser.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成JWT token
        return jwtUtils.generateToken(dbUser.getId());
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    @Override
    public SysUser getCurrentUser() {
        return getById(UserContextUtil.getCurrentUserId());
    }

    /**
     * 根据ID获取用户
     *
     * @param userId 用户ID
     * @return 用户
     */
    @Override
    public SysUser getById(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    /**
     * 登出
     */
    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 获取用户角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<String> getUserRoles(Long userId) {
        return sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(ur -> sysRoleMapper.selectById(ur.getRoleId())).map(SysRole::getRoleCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @Override
    public UserDetails getUserDetails(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getStatus() == 0) {
            return null;
        }
        List<SimpleGrantedAuthority> authorities = getUserRoles(userId).stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.getStatus() == 1, true, true, true, authorities);
    }

    /**
     * 获取用户列表
     *
     * @return 用户列表
     */
    @Override
    public List<SysUser> list() {
        List<SysUser> users = sysUserMapper.selectList(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeleted, 0).orderByDesc(SysUser::getCreatedTime));

        // 查询用户角色信息
        for (SysUser user : users) {
            List<SysUserRole> userRoles = sysUserRoleMapper
                    .selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));

            // 设置角色ID列表
            user.setRoleIds(userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));

            // 设置角色列表
            user.setRoles(userRoles.stream().map(ur -> sysRoleMapper.selectById(ur.getRoleId()))
                    .collect(Collectors.toList()));
        }

        return users;
    }

    /**
     * 创建用户
     *
     * @param user 用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(SysUser user) {
        // 检查用户名是否已存在
        if (sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, user.getUsername())
                .eq(SysUser::getDeleted, 0)) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setDeleted(0);

        // 保存用户
        sysUserMapper.insert(user);

        // 保存用户角色关系
        updateUserRoles(user);
    }

    /**
     * 更新用户角色关系
     *
     * @param user 用户
     */
    private void updateUserRoles(SysUser user) {
        if (CollectionUtils.isEmpty(user.getRoleIds())) {
            return;
        }
        List<SysUserRole> userRoles = user.getRoleIds().stream().map(roleId -> {
            // 检查角色是否存在
            SysRole role = sysRoleMapper.selectById(roleId);
            if (role == null || role.getDeleted() == 1) {
                throw new BusinessException("角色不存在：" + roleId);
            }
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            return userRole;
        }).collect(Collectors.toList());
        userRoles.forEach(sysUserRoleMapper::insert);
    }

    /**
     * 更新用户
     *
     * @param user 用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUser user) {
        // 检查用户是否存在
        SysUser dbUser = sysUserMapper.selectById(user.getId());
        if (dbUser == null || dbUser.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        user.setUsername(null);
        user.setPassword(null);
        sysUserMapper.updateById(user);

        // 更新用户角色关系
        if (user.getRoleIds() != null) {
            // 删除原有角色关系
            sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
            // 添加新的角色关系
            updateUserRoles(user);
        }
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Override
    public void deleteUser(Long id) {
        // 检查用户是否存在
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        // 软删除用户
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setDeleted(1);
        sysUserMapper.updateById(updateUser);

        // 删除用户角色关系
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    /**
     * 修改用户状态
     *
     * @param id 用户ID
     */
    @Override
    public void changeUserStatus(Long id) {
        // 检查用户是否存在
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户状态
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setStatus(user.getStatus() == 1 ? 0 : 1);
        sysUserMapper.updateById(updateUser);
    }

    /**
     * 更新当前用户信息
     *
     * @param userInfo 用户信息
     */
    @Override
    public void updateCurrentUserInfo(UserInfoVO userInfo) {
        Long userId = UserContextUtil.getCurrentUserId();
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setRealName(userInfo.getRealName());
        user.setEmail(userInfo.getEmail());
        updateById(user);
    }

    /**
     * 更新当前用户密码
     *
     * @param updatePassword 更新密码VO
     */
    @Override
    public void updateCurrentUserPassword(UpdatePasswordVO updatePassword) {
        Long userId = UserContextUtil.getCurrentUserId();
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(updatePassword.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(updatePassword.getNewPassword()));
        updateById(user);
    }
}