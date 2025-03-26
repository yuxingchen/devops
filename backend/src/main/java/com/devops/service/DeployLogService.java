package com.devops.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.entity.DeployLog;

import java.util.List;

/**
 * 部署日志服务接口
 */
public interface DeployLogService extends IService<DeployLog> {
    
    /**
     * 记录部署日志
     *
     * @param deployId  部署记录ID
     * @param serverId  服务器ID
     * @param logType   日志类型
     * @param content   日志内容
     */
    void log(Long deployId, Long serverId, Integer logType, String content);
    
    /**
     * 获取部署记录的日志
     *
     * @param deployId 部署记录ID
     * @return 日志列表
     */
    List<DeployLog> getLogsByDeployId(Long deployId);

} 