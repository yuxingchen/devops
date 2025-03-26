package com.devops.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.entity.DeployLog;
import com.devops.mapper.DeployLogMapper;
import com.devops.service.DeployLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 部署日志服务实现类
 */
@Service
@RequiredArgsConstructor
public class DeployLogServiceImpl extends ServiceImpl<DeployLogMapper, DeployLog> implements DeployLogService {

    /**
     * 记录部署日志
     *
     * @param deployId 部署ID
     * @param serverId 服务器ID
     * @param logType  日志类型
     * @param content  日志内容
     */
    @Override
    public void log(Long deployId, Long serverId, Integer logType, String content) {
        DeployLog log = new DeployLog();
        log.setDeployId(deployId);
        log.setServerId(serverId);
        log.setLogType(logType);
        log.setLogContent(content);
        log.setCreatedTime(new Date());
        
        save(log);
    }
    
    /**
     * 根据部署ID查询部署日志
     *
     * @param deployId 部署ID
     * @return 部署日志列表
     */
    @Override
    public List<DeployLog> getLogsByDeployId(Long deployId) {
        return baseMapper.findByDeployId(deployId);
    }

} 