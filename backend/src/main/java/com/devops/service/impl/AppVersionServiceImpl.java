package com.devops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.common.BusinessException;
import com.devops.entity.AppVersion;
import com.devops.entity.Application;
import com.devops.mapper.AppVersionMapper;
import com.devops.service.AppVersionService;
import com.devops.service.ApplicationService;
import com.devops.util.FileUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * 应用版本服务实现类
 *
 * @author yux
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppVersionServiceImpl extends ServiceImpl<AppVersionMapper, AppVersion> implements AppVersionService {

    private final MinioClient minioClient;
    private final ApplicationService applicationService;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * 检查文件是否重复
     *
     * @param appId            应用ID
     * @param fileMd5          文件MD5值
     * @param excludeVersionId 需要排除的版本ID
     * @return 重复的版本信息，如果不重复返回null
     */
    private AppVersion checkFileDuplicate(Long appId, String fileMd5, Long excludeVersionId) {
        LambdaQueryWrapper<AppVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppVersion::getAppId, appId)
                .eq(AppVersion::getFileMd5, fileMd5)
                .ne(excludeVersionId != null, AppVersion::getId, excludeVersionId);

        return getOne(queryWrapper);
    }

    /**
     * 上传版本
     *
     * @param file       文件
     * @param appId      应用ID
     * @param versionName 版本名称
     * @param versionCode 版本编码
     * @param description 版本描述
     * @return 版本信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppVersion upload(MultipartFile file, Long appId, String versionName, String versionCode, String description) {
        // 检查应用是否存在
        Application application = checkAndGetApplication(appId);

        // 检查版本号是否已存在
        checkVersionCodeExists(appId, versionCode);

        try {
            // 检查文件MD5
            String fileMd5 = versionMd5Check(file, appId);
            // 上传文件并获取文件信息
            FileInfo fileInfo = uploadFile(file, application.getAppCode());

            // 保存版本信息
            AppVersion version = new AppVersion();
            version.setAppId(appId);
            version.setVersionName(versionName);
            version.setVersionCode(versionCode);
            version.setFileName(fileInfo.fileName);
            version.setFilePath(fileInfo.objectName);
            version.setFileSize(fileInfo.fileSize);
            version.setFileMd5(fileMd5);
            version.setDescription(description);

            save(version);

            // 设置关联的应用信息
            version.setApplication(application);
            return version;
        } catch (Exception e) {
            log.error("上传版本失败", e);
            throw new BusinessException("上传版本失败：" + e.getMessage());
        }
    }

    /**
     * 版本MD5校验
     *
     * @param file  文件
     * @param appId 应用ID
     * @return MD5值
     */
    @NotNull
    private String versionMd5Check(MultipartFile file, Long appId) {
        // 计算文件MD5
        String fileMd5 = FileUtil.calculateMD5(file);
        if (fileMd5 == null) {
            throw new BusinessException("计算文件MD5失败");
        }

        // 检查文件是否重复
        AppVersion duplicateVersion = checkFileDuplicate(appId, fileMd5, null);
        if (duplicateVersion != null) {
            throw new BusinessException(String.format("文件与版本 %s(%s) 重复",
                    duplicateVersion.getVersionName(), duplicateVersion.getVersionCode()));
        }
        return fileMd5;
    }

    /**
     * 更新版本
     *
     * @param file       文件
     * @param id         版本ID
     * @param appId      应用ID
     * @param versionName 版本名称
     * @param versionCode 版本编码
     * @param description 版本描述
     * @return 版本信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppVersion update(MultipartFile file, Long id, Long appId, String versionName, String versionCode, String description) {
        // 检查版本是否存在
        AppVersion version = checkAndGetVersion(id);

        // 检查应用是否存在
        Application application = checkAndGetApplication(appId);

        try {
            // 如果上传了新文件
            if (file != null) {
                // 计算文件MD5
                String fileMd5 = versionMd5Check(file, appId);

                // 上传新文件
                FileInfo fileInfo = uploadFile(file, application.getAppCode());

                // 删除旧文件
                deleteFile(version.getFilePath());

                // 更新文件相关信息
                version.setFileName(fileInfo.fileName);
                version.setFilePath(fileInfo.objectName);
                version.setFileSize(fileInfo.fileSize);
                version.setFileMd5(fileMd5);
            }

            // 更新基本信息
            version.setVersionName(versionName);
            version.setDescription(description);

            updateById(version);

            // 设置关联的应用信息
            version.setApplication(application);
            return version;
        } catch (Exception e) {
            log.error("更新版本失败", e);
            throw new BusinessException("更新版本失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询版本
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param appId    应用ID
     * @param keyword  关键词
     * @return 版本列表
     */
    @Override
    public IPage<AppVersion> listVersions(Integer pageNum, Integer pageSize, Long appId, String keyword) {
        Page<AppVersion> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<AppVersion> queryWrapper = new LambdaQueryWrapper<>();
        if (appId != null) {
            queryWrapper.eq(AppVersion::getAppId, appId);
        }

        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(AppVersion::getVersionName, keyword)
                    .or()
                    .like(AppVersion::getVersionCode, keyword)
            );
        }

        queryWrapper.orderByDesc(AppVersion::getCreatedTime);

        return page(page, queryWrapper);
    }

    /**
     * 检查并获取应用信息
     */
    private Application checkAndGetApplication(Long appId) {
        Application application = applicationService.getById(appId);
        if (application == null) {
            throw new BusinessException("应用不存在");
        }
        return application;
    }

    /**
     * 检查并获取版本信息
     */
    private AppVersion checkAndGetVersion(Long id) {
        AppVersion version = getById(id);
        if (version == null) {
            throw new BusinessException("版本不存在");
        }
        return version;
    }

    /**
     * 检查版本号是否已存在
     */
    private void checkVersionCodeExists(Long appId, String versionCode) {
        AppVersion existVersion = lambdaQuery()
                .eq(AppVersion::getAppId, appId)
                .eq(AppVersion::getVersionCode, versionCode)
                .one();

        if (existVersion != null) {
            throw new BusinessException("版本号已存在");
        }
    }

    /**
     * 上传文件到MinIO
     */
    private FileInfo uploadFile(MultipartFile file, String appCode) throws Exception {
        String fileName = file.getOriginalFilename();
        String objectName = String.format("%s/%s/%s", appCode, UUID.randomUUID(), fileName);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        return new FileInfo(fileName, objectName, file.getSize());
    }

    /**
     * 从MinIO删除文件
     */
    private void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("删除文件失败: {}", objectName, e);
        }
    }

    /**
     * 文件信息内部类
     */
    private static class FileInfo {
        final String fileName;
        final String objectName;
        final long fileSize;

        FileInfo(String fileName, String objectName, long fileSize) {
            this.fileName = fileName;
            this.objectName = objectName;
            this.fileSize = fileSize;
        }
    }


}