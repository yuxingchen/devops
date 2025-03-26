package com.devops.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.entity.SysUser;
import com.devops.vo.UpdatePasswordVO;
import com.devops.vo.UserInfoVO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 系统用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 用户登录
     *
     * @param user 登录信息
     * @return JWT token
     */
    String login(SysUser user);
    
    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    SysUser getCurrentUser();
    
    /**
     * 用户登出
     */
    void logout();
    
    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getUserRoles(Long userId);
    
    /**
     * 获取用户认证信息
     *
     * @param userId 用户ID
     * @return 用户认证信息
     */
    UserDetails getUserDetails(Long userId);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getById(Long userId);
    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    List<SysUser> list();
    /**
     * 创建用户
     *
     * @param user 用户信息
     */
    void createUser(SysUser user);
    /**
     * 修改用户
     *
     * @param user 用户信息
     */
    void updateUser(SysUser user);
    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);
    /**
     * 修改用户状态
     *
     * @param id 用户ID
     */
    void changeUserStatus(Long id);

    /**
     * 更新当前用户信息
     *
     * @param userInfo 用户信息
     */
    void updateCurrentUserInfo(UserInfoVO userInfo);
    
    /**
     * 修改当前用户密码
     *
     * @param updatePassword 密码信息
     */
    void updateCurrentUserPassword(UpdatePasswordVO updatePassword);
}