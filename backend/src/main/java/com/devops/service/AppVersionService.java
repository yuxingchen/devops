package com.devops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.entity.AppVersion;
import org.springframework.web.multipart.MultipartFile;

/**
 * 应用版本服务接口
 */
public interface AppVersionService extends IService<AppVersion> {

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
    AppVersion upload(MultipartFile file, Long appId, String versionName, String versionCode, String description);

    /**
     * 更新版本
     *
     * @param file        版本文件（可选）
     * @param id         版本ID
     * @param appId      应用ID
     * @param versionName 版本名称
     * @param versionCode 版本号
     * @param description 描述
     * @return 版本信息
     */
    AppVersion update(MultipartFile file, Long id, Long appId, String versionName, String versionCode, String description);

    /**
     * 分页查询版本列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param appId    应用ID
     * @param keyword  关键字
     * @return 版本列表
     */
    IPage<AppVersion> listVersions(Integer pageNum, Integer pageSize, Long appId, String keyword);
} 