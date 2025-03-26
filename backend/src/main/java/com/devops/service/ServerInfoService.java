package com.devops.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.entity.ServerInfo;

import java.util.List;

/**
 * 服务器信息服务接口
 */
public interface ServerInfoService extends IService<ServerInfo> {
    
    /**
     * 根据环境ID获取服务器列表
     *
     * @param envId 环境ID
     * @return 服务器列表
     */
    List<ServerInfo> listByEnvId(Long envId);
    
    /**
     * 获取服务器列表，支持模糊查询
     *
     * @param envId 环境ID
     * @param serverName 服务器名称（模糊查询）
     * @param serverIp 服务器IP（模糊查询）
     * @return 服务器列表
     */
    List<ServerInfo> listServers(Long envId, String serverName, String serverIp);
    
    /**
     * 测试服务器连接
     *
     * @param id 服务器ID
     * @return 连接是否成功
     */
    Boolean testConnection(Long id);

} 