package com.devops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devops.common.BusinessException;
import com.devops.entity.*;
import com.devops.mapper.DeployRecordMapper;
import com.devops.service.*;
import com.devops.util.SshUtil;
import com.devops.util.TimeUtils;
import com.devops.vo.CommandStep;
import com.devops.vo.DeployCommandVO;
import com.devops.vo.DeployRecordVO;
import com.jcraft.jsch.Session;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 部署服务实现类
 *
 * @author yux
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeployServiceImpl implements DeployService {

    private final ApplicationService applicationService;
    private final ServerInfoService serverInfoService;
    private final AppVersionService appVersionService;
    private final DeployRecordMapper deployRecordMapper;
    private final DeployLogService deployLogService;
    private final SysUserService sysUserService;
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * 预览部署命令
     *
     * @param appId         应用ID
     * @param versionId     版本ID
     * @param serverIds     部署服务器ID
     * @param backup        是否备份
     * @param backupType    备份类型 full-全量备份，incremental-增量备份
     * @param deployCommand 部署命令
     * @return 部署命令列表
     */
    @Override
    public List<DeployCommandVO> previewDeployCommands(Long appId, Long versionId, String serverIds, Boolean backup,
                                                       String backupType, String deployCommand) {
        // 获取服务器列表
        List<ServerInfo> servers = serverInfoService.listByIds(Arrays.asList(serverIds.split(",")));
        return servers.stream().map(server -> {
            DeployCommandVO vo = new DeployCommandVO();
            vo.setServerName(server.getServerName());
            // 构建命令步骤列表
            List<CommandStep> commandSteps = getDeployCommands(server.getId(), appId, versionId, backup, backupType,
                    new Date(), deployCommand);
            vo.setCommands(commandSteps);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 部署
     *
     * @param appId         应用ID
     * @param versionId     版本ID
     * @param serverIds     部署服务器ID
     * @param backup        是否备份
     * @param backupType    备份类型 full-全量备份，incremental-增量备份
     * @param deployCommand 部署命令
     * @return 部署记录
     */
    @Override
    public DeployRecord deploy(Long appId, Long versionId, String serverIds, Boolean backup, String backupType,
                               String deployCommand) {
        // 检查应用信息
        Application application = applicationService.getDetail(appId);
        if (application == null) {
            throw new BusinessException("部署应用不存在");
        }

        // 检查版本信息
        AppVersion version = appVersionService.getById(versionId);
        if (version == null) {
            throw new BusinessException("版本不存在");
        }

        // 检查服务器信息
        Long[] servers = application.getServers();
        if (StringUtils.isNotBlank(serverIds)) {
            servers = Arrays.stream(serverIds.split(",")).map(Long::parseLong).toArray(Long[]::new);
        }
        List<ServerInfo> serversList = serverInfoService.listByIds(Arrays.asList(servers));
        if (serversList.isEmpty()) {
            throw new BusinessException("未找到有效的服务器");
        }

        // 获取当前时间
        Date startTime = TimeUtils.getNowDate();
        // 创建部署记录
        DeployRecord record = new DeployRecord();
        record.setAppId(appId);
        record.setVersionId(versionId);
        record.setServerIds(Arrays.stream(servers).map(String::valueOf).collect(Collectors.joining(",")));
        record.setDeployStatus(0);
        record.setStartTime(startTime);
        deployRecordMapper.insert(record);

        try {
            // 遍历服务器执行部署
            for (ServerInfo server : serversList) {
                Session session = null;
                Long serverId = server.getId();
                try {
                    // 从MinIO下载文件
                    InputStream fileStream = minioClient
                            .getObject(GetObjectArgs.builder().bucket(bucketName).object(version.getFilePath()).build());
                    logStatus(record.getId(), serverId,
                            String.format("开始部署到服务器：%s(%s)", server.getServerName(), server.getServerIp()));
                    session = SshUtil.connectToServer(server);
                    String deployPath = server.getUserPath() + application.getDeployPath();
                    // 获取部署命令列表
                    List<CommandStep> deployCommands = getDeployCommands(serverId, appId, versionId, backup, backupType,
                            startTime, deployCommand);
                    // 执行部署命令
                    for (CommandStep command : deployCommands) {
                        String exeCommand = command.getCommand();
                        if ("java".equals(command.getType())) {
                            if (exeCommand.startsWith("upload_file")) {
                                // 处理文件上传
                                logFileUpload(record.getId(), serverId, deployPath, version.getFileName());
                                SshUtil.uploadFile(session, fileStream, deployPath, version.getFileName());
                            }
                        } else {
                            // 执行普通命令
                            executeCommandWithLog(record.getId(), serverId, exeCommand, session);
                        }
                    }
                    logStatus(record.getId(), serverId, "部署完成");
                    // 删除MinIO文件
                    minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(version.getFilePath()).build());
                } catch (Exception e) {
                    logStatus(record.getId(), serverId, "部署失败：" + e.getMessage());
                    throw e;
                } finally {
                    SshUtil.closeSession(session);
                }
            }
            record.setDeployStatus(1);
            record.setEndTime(new Date());
            deployRecordMapper.updateById(record);
            return record;
        } catch (Exception e) {
            log.error("部署失败", e);
            record.setDeployStatus(2);
            record.setEndTime(new Date());
            deployRecordMapper.updateById(record);
            throw new BusinessException("部署失败：" + e.getMessage());
        }
    }

    /**
     * 回滚
     *
     * @param recordId 部署记录ID
     * @return 部署记录
     */
    @Override
    public DeployRecord rollback(Long recordId) {
        DeployRecord record = deployRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("部署记录不存在");
        }
        // 更新记录的更新人
        deployRecordMapper.updateById(record);
        String serverIds = record.getServerIds();
        try {
            List<Long> serverIdList = Stream.of(serverIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
            List<ServerInfo> servers = serverInfoService.listByIds(serverIdList);
            AppVersion version = appVersionService.getById(record.getVersionId());
            Application application = applicationService.getById(record.getAppId());
            // 应用备份路径
            for (ServerInfo server : servers) {
                Session session = null;
                try {
                    logStatus(recordId, server.getId(),
                            String.format("开始回滚服务器：%s(%s)", server.getServerName(), server.getServerIp()));
                    session = SshUtil.connectToServer(server);
                    // 查找对应备份
                    String userPath = server.getUserPath();
                    String versionFileName = version.getFileName();
                    String backupVersionPath = getBackupPath(record.getStartTime(), userPath, application) + "/"
                            + versionFileName;
                    String findBackupCmd = String.format("ls -t %s 2> /dev/null | wc -l", backupVersionPath);
                    String latestBackup = executeCommandWithLog(recordId, server.getId(), findBackupCmd, session)
                            .trim();

                    if ("0".equals(latestBackup)) {
                        String errorMsg = String.format("未找到可回滚的版本: %s", backupVersionPath);
                        throw new BusinessException(errorMsg);
                    }
                    // 获取部署路径
                    String deployPath = userPath + application.getDeployPath();
                    // 部署版本文件
                    String deployVersionFile = deployPath + "/" + versionFileName;
                    // 恢复备份
                    String restoreCmd = String.format("cp %s %s", backupVersionPath, deployVersionFile);
                    executeCommandWithLog(recordId, server.getId(), restoreCmd, session);

                    // 启动服务
                    String startCmd = getDeployCommand(versionFileName, application.getAppCode());
                    String executeCmd = String.format("cd %s && %s", deployPath, startCmd);
                    executeCommandWithLog(recordId, server.getId(), executeCmd, session);

                    logStatus(recordId, server.getId(), "回滚完成");
                } catch (Exception e) {
                    logStatus(recordId, server.getId(), "回滚失败：" + e.getMessage());
                    throw e;
                } finally {
                    SshUtil.closeSession(session);
                }
            }

            return record;
        } catch (Exception e) {
            log.error("回滚失败", e);
            throw new BusinessException("回滚失败：" + e.getMessage());
        }
    }

    /**
     * 获取部署记录
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param appId    应用ID
     * @param serverId 服务器ID
     * @return IPage<DeployRecordVO> 部署记录
     */
    @Override
    public IPage<DeployRecordVO> listRecords(Integer pageNum, Integer pageSize, Long appId, Long serverId) {
        // 创建分页对象
        Page<DeployRecord> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<DeployRecord> wrapper = new LambdaQueryWrapper<>();
        if (appId != null) {
            wrapper.eq(DeployRecord::getAppId, appId);
        }
        if (serverId != null) {
            wrapper.apply("FIND_IN_SET({0}, server_ids)", serverId.toString());
        }
        // 按创建时间倒序排序
        wrapper.orderByDesc(DeployRecord::getCreatedTime);
        // 执行分页查询
        IPage<DeployRecord> recordPage = deployRecordMapper.selectPage(page, wrapper);
        return recordPage.convert(record -> {
            DeployRecordVO vo = new DeployRecordVO();
            BeanUtils.copyProperties(record, vo);

            // 填充应用信息
            Application app = applicationService.getById(record.getAppId());
            if (app != null) {
                vo.setAppName(app.getAppCode() + "(" + app.getAppName() + ")");
            }

            // 填充版本信息
            AppVersion version = appVersionService.getById(record.getVersionId());
            if (version != null) {
                vo.setVersionName(version.getVersionName());
                vo.setVersionCode(version.getVersionCode());
            }

            // 填充服务器信息
            if (StringUtils.isNotEmpty(record.getServerIds())) {
                List<Long> serverIds = Arrays.stream(record.getServerIds().split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toList());
                List<ServerInfo> servers = serverInfoService.listByIds(serverIds);
                vo.setServerNames(servers.stream()
                        .map(server -> server.getServerName() + "(" + server.getServerIp() + ")")
                        .collect(Collectors.joining(", ")));
            }

            // 填充创建人信息
            if (record.getCreatedBy() != null) {
                SysUser user = sysUserService.getById(record.getCreatedBy());
                if (user != null) {
                    vo.setCreatedByName(user.getUsername());
                }
            }

            return vo;
        });
    }

    /**
     * 获取部署记录的部署日志
     *
     * @param recordId 部署记录ID
     * @return 部署日志列表
     */
    @Override
    public List<DeployLog> getDeployLogs(Long recordId) {
        return deployLogService.getLogsByDeployId(recordId);
    }

    /**
     * 获取部署命令列表
     *
     * @param serverId      服务ID
     * @param appId         应用ID
     * @param versionId     版本ID
     * @param backup        是否备份 true-备份，false-不备份
     * @param backupType    备份类型 full-全量备份，incremental-增量备份
     * @param deployCommand 部署命令
     * @return 命令列表
     */
    private List<CommandStep> getDeployCommands(Long serverId, Long appId, Long versionId, Boolean backup,
                                                String backupType, Date startTime, String deployCommand) {
        List<CommandStep> commands = new ArrayList<>();
        ServerInfo server = serverInfoService.getById(serverId);
        Application application = applicationService.getById(appId);
        AppVersion version = appVersionService.getById(versionId);
        String userPath = server.getUserPath();
        String fileName = version.getFileName();
        String backupPath = getBackupPath(startTime, userPath, application);
        String deployPath = userPath + application.getDeployPath();
        String filePath = deployPath + "/" + fileName;
        int commandOrder = 1;

        // 备份命令
        boolean isJarOrWar = fileName.endsWith(".jar") || fileName.endsWith(".war");
        if (backup) {
            if (isJarOrWar) {
                // JAR/WAR包备份：直接复制文件到备份目录
                String backupCommand = String.format("mkdir -p %s && cp %s %s/%s 2>/dev/null || true",
                        backupPath, filePath, backupPath, fileName);
                commands.add(new CommandStep(commandOrder++, "备份", backupCommand, "shell"));
            } else if (fileName.endsWith(".zip")) {
                if ("full".equals(backupType)) {
                    // ZIP包全量备份：将部署目录下的所有文件打包
                    String backupCommand = getZipFullBackupCommand(backupPath, deployPath, fileName);
                    commands.add(new CommandStep(commandOrder++, "全量备份", backupCommand, "shell"));
                }
            }
        }

        // 上传文件标记
        commands.add(new CommandStep(commandOrder++, "上传文件", "upload_file", "java"));

        // ZIP包增量备份：在文件上传后执行，因为需要读取新上传的文件内容
        if (backup && fileName.endsWith(".zip") && "incremental".equals(backupType)) {
            String backupCommand = getZipIncrementalBackupCommand(backupPath, deployPath, fileName);
            commands.add(new CommandStep(commandOrder++, "增量备份", backupCommand, "shell"));
        }

        // 部署服务命令
        if (StringUtils.isEmpty(deployCommand)) {
            deployCommand = getDeployCommand(fileName, application.getAppCode());
        } else {
            // 针对部署命令进行安全校验，禁止非法命令
            SshUtil.isValidCommand(deployCommand);
        }
        commands.add(new CommandStep(commandOrder++, "部署", String.format("cd %s && %s", deployPath, deployCommand),
                "shell"));
        return commands;
    }

    /**
     * 获取备份路径
     *
     * @param startTime   备份时间
     * @param userPath    用户路径
     * @param application 应用信息
     * @return 备份路径
     */
    @NotNull
    private static String getBackupPath(Date startTime, String userPath, Application application) {
        return userPath + application.getBackupPath() + "/" + TimeUtils.formatTimeLong(startTime);
    }

    /**
     * 获取部署命令
     *
     * @param fileName 文件名
     * @param appCode  应用编码
     * @return 部署命令
     */
    private String getDeployCommand(String fileName, String appCode) {
        if (fileName.endsWith(".jar") || fileName.endsWith(".war")) {
            return String.format("sh startup_%s.sh > /dev/null 2>&1 &", appCode);
        } else if (fileName.endsWith(".zip")) {
            return String.format("unzip -o %s", fileName);
        }
        return null;
    }

    /**
     * 获取zip包全量备份命令
     *
     * @param backupPath 备份路径
     * @param deployPath 部署路径
     * @param fileName   文件名
     * @return 备份命令
     */
    private String getZipFullBackupCommand(String backupPath, String deployPath, String fileName) {
        return String.format(
                "mkdir -p %s && cd %s && zip -r %s/%s .",
                backupPath, deployPath, backupPath, fileName
        );
    }

    /**
     * 获取zip包增量备份命令
     *
     * @param backupPath 备份路径
     * @param deployPath 部署路径
     * @param fileName   文件名
     * @return 备份命令
     */
    private String getZipIncrementalBackupCommand(String backupPath, String deployPath, String fileName) {
        String versionFile = String.format("%s/%s", deployPath, fileName);
        return String.format(
                // 1. 定义,创建备份目录;定义临时目录;定义临时文件
                "BACK_DIR=\"%s\" && mkdir -p \"$BACK_DIR\" && TEMP_DIR=$(mktemp -d) && TEMP_TXT=\"$BACK_DIR/file_list.txt\" " +
                        // 2. 解压新版本到临时目录
                        "&& unzip -q \"%s\" -d \"$TEMP_DIR\" && " +
                        // 3. 生成文件列表（包含子目录）; 切换到部署目录
                        "cd \"$TEMP_DIR\" && find . -type f > \"$TEMP_TXT\" && cd \"%s\" && " +
                        // 4. 读取文件列表，检查并备份文件（保持目录结构）
                        "while IFS= read -r file; do " +
                        "  if [ -f \"${file#./}\" ]; then " +
                        "    zip -g \"$BACK_DIR/%s\" \"${file#./}\"; " +
                        "  fi; " +
                        "done < \"$TEMP_TXT\" && " +
                        // 5. 清理临时文件和目录
                        "rm -f \"$TEMP_TXT\" && rm -rf \"$TEMP_DIR\"",
                backupPath, versionFile,
                deployPath, fileName
        );
    }

    /**
     * 执行命令并记录日志
     *
     * @param recordId 部署记录ID
     * @param serverId 服务器ID
     * @param command  要执行的命令
     * @param session  SSH会话
     * @return 命令执行结果
     * @throws BusinessException 业务异常
     */
    private String executeCommandWithLog(Long recordId, Long serverId, String command, Session session)
            throws BusinessException {
        try {
            deployLogService.log(recordId, serverId, 1, "执行命令: " + command);
            String result = SshUtil.executeCommand(session, command);
            if (!result.isEmpty()) {
                deployLogService.log(recordId, serverId, 1, "命令输出: " + result);
            }
            return result;
        } catch (Exception e) {
            String errorMsg = "执行命令失败: " + e.getMessage();
            deployLogService.log(recordId, serverId, 1, errorMsg);
            throw new BusinessException(errorMsg);
        }
    }

    /**
     * 记录文件上传日志
     *
     * @param recordId   部署记录ID
     * @param serverId   服务器ID
     * @param deployPath 部署路径
     * @param fileName   文件名
     */
    private void logFileUpload(Long recordId, Long serverId, String deployPath, String fileName) {
        String uploadMsg = String.format("上传文件到目录: %s, 文件名: %s", deployPath, fileName);
        deployLogService.log(recordId, serverId, 1, uploadMsg);
    }

    /**
     * 记录操作状态日志
     *
     * @param recordId 部署记录ID
     * @param serverId 服务器ID
     * @param message  日志消息
     */
    private void logStatus(Long recordId, Long serverId, String message) {
        deployLogService.log(recordId, serverId, 0, message);
    }

}