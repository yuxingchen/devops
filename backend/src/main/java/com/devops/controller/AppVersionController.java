package com.devops.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.devops.common.Result;
import com.devops.entity.AppVersion;
import com.devops.service.AppVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 应用版本管理控制器
 * 提供版本上传、查询和删除等功能
 *
 * @author yux
 */
@RestController
@RequestMapping("/api/versions")
@RequiredArgsConstructor
public class AppVersionController {

    private final AppVersionService appVersionService;

    /**
     * 获取版本列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param appId    应用ID
     * @param keyword  关键字
     * @return 版本列表
     */
    @GetMapping
    public Result<IPage<AppVersion>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long appId,
            @RequestParam(required = false) String keyword) {
        return Result.success(appVersionService.listVersions(pageNum, pageSize, appId, keyword));
    }

    /**
     * 获取版本详情
     */
    @GetMapping("/{id}")
    public Result<AppVersion> get(@PathVariable Long id) {
        return Result.success(appVersionService.getById(id));
    }

    /**
     * 上传版本
     *
     * @param file        版本文件
     * @param appId       应用ID
     * @param versionName 版本名称
     * @param versionCode 版本号
     * @param description 描述
     * @return 版本信息
     */
    @PostMapping("/upload")
    public Result<AppVersion> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("appId") Long appId,
            @RequestParam("versionName") String versionName,
            @RequestParam("versionCode") String versionCode,
            @RequestParam(value = "description", required = false) String description) {
        return Result.success(appVersionService.upload(file, appId, versionName, versionCode, description));
    }

    /**
     * 更新版本
     *
     * @param file        版本文件（可选）
     * @param id          版本ID
     * @param appId       应用ID
     * @param versionName 版本名称
     * @param versionCode 版本号
     * @param description 描述
     * @return 版本信息
     */
    @PostMapping("/update")
    public Result<AppVersion> update(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("id") Long id,
            @RequestParam("appId") Long appId,
            @RequestParam("versionName") String versionName,
            @RequestParam("versionCode") String versionCode,
            @RequestParam(value = "description", required = false) String description) {
        return Result.success(appVersionService.update(file, id, appId, versionName, versionCode, description));
    }

    /**
     * 删除版本
     *
     * @param id 版本ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(appVersionService.removeById(id));
    }


} 