package com.devops.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.devops.entity.DeployLog;
import com.devops.entity.DeployRecord;
import com.devops.vo.DeployCommandVO;
import com.devops.vo.DeployRecordVO;

import java.util.List;

/**
 * 部署服务接口
 */
public interface DeployService {

    /**
     * 预览部署命令
     *
     * @param appId         应用ID
     * @param versionId     版本ID
     * @param serverIds     服务器ID列表（逗号分隔）
     * @param backup        是否备份
     * @param backupType    备份类型 full-全量备份，incremental-增量备份
     * @param deployCommand 部署命令
     * @return 部署命令列表
     */
    List<DeployCommandVO> previewDeployCommands(Long appId, Long versionId, String serverIds, Boolean backup, String backupType, String deployCommand);

    /**
     * 部署
     *
     * @param appId         应用ID
     * @param versionId     版本ID
     * @param serverIds     服务器ID列表（逗号分隔）
     * @param backup        是否备份
     * @param backupType    备份类型 full-全量备份，incremental-增量备份
     * @param deployCommand 部署命令
     * @return 部署记录
     */
    DeployRecord deploy(Long appId, Long versionId, String serverIds, Boolean backup, String backupType, String deployCommand);

    /**
     * 回滚部署
     *
     * @param recordId 部署记录ID
     * @return 部署记录
     */
    DeployRecord rollback(Long recordId);

    /**
     * 分页查询部署记录
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param appId    应用ID
     * @param serverId 服务器ID
     * @return 部署记录分页列表
     */
    IPage<DeployRecordVO> listRecords(Integer pageNum, Integer pageSize, Long appId, Long serverId);

    /**
     * 获取部署日志
     *
     * @param recordId 部署记录ID
     * @return 部署日志列表
     */
    List<DeployLog> getDeployLogs(Long recordId);
} 