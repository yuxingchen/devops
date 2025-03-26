package com.devops.controller;

import com.devops.common.Result;
import com.devops.entity.SysUser;
import com.devops.service.SysUserService;
import com.devops.vo.UpdatePasswordVO;
import com.devops.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yux
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {
    
    private final SysUserService sysUserService;
    
    @PostMapping("/login")
    public Result<String> login(@RequestBody SysUser user) {
        return Result.success(sysUserService.login(user));
    }
    
    @GetMapping("/info")
    public Result<SysUser> getUserInfo() {
        return Result.success(sysUserService.getCurrentUser());
    }
    
    @PostMapping("/logout")
    public Result<Void> logout() {
        sysUserService.logout();
        return Result.success();
    }
    
    @GetMapping
    public Result<List<SysUser>> list() {
        return Result.success(sysUserService.list());
    }
    
    @PostMapping
    public Result<Void> create(@RequestBody SysUser user) {
        sysUserService.createUser(user);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        sysUserService.updateUser(user);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id) {
        sysUserService.changeUserStatus(id);
        return Result.success();
    }

    /**
     * 更新当前用户信息
     *
     * @param userInfo 用户信息
     * @return 更新结果
     */
    @PutMapping("/info")
    public Result<Void> updateInfo(@RequestBody @Valid UserInfoVO userInfo) {
        sysUserService.updateCurrentUserInfo(userInfo);
        return Result.success();
    }

    /**
     * 修改当前用户密码
     *
     * @param updatePassword 密码信息
     * @return 修改结果
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody @Valid UpdatePasswordVO updatePassword) {
        sysUserService.updateCurrentUserPassword(updatePassword);
        return Result.success();
    }
}