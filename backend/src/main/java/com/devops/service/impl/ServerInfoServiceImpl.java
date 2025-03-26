package com.devops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.common.BusinessException;
import com.devops.entity.ServerInfo;
import com.devops.mapper.ServerInfoMapper;
import com.devops.service.ServerInfoService;
import com.devops.util.AESUtil;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Properties;

/**
 * 服务器信息服务实现类
 *
 * @author yux
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServerInfoServiceImpl extends ServiceImpl<ServerInfoMapper, ServerInfo> implements ServerInfoService {

    /**
     * 根据环境ID查询服务器信息
     *
     * @param envId 环境ID
     * @return 服务器信息列表
     */
    @Override
    public List<ServerInfo> listByEnvId(Long envId) {
        return baseMapper.findByEnvId(envId);
    }

    /**
     * 查询服务器信息
     *
     * @param envId      环境ID
     * @param serverName 服务器名称
     * @param serverIp   服务器IP
     * @return 服务器信息列表
     */
    @Override
    public List<ServerInfo> listServers(Long envId, String serverName, String serverIp) {
        LambdaQueryWrapper<ServerInfo> wrapper = new LambdaQueryWrapper<ServerInfo>()
                .eq(ServerInfo::getDeleted, 0)
                .eq(envId != null, ServerInfo::getEnvId, envId)
                .like(StringUtils.hasText(serverName), ServerInfo::getServerName, serverName)
                .like(StringUtils.hasText(serverIp), ServerInfo::getServerIp, serverIp);

        return list(wrapper);
    }

    /**
     * 测试服务器连接
     *
     * @param id 服务器ID
     * @return 是否成功
     */
    @Override
    public Boolean testConnection(Long id) {
        ServerInfo serverInfo = getById(id);
        if (serverInfo == null) {
            throw new BusinessException("服务器信息不存在");
        }

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(
                    serverInfo.getUsername(),
                    serverInfo.getServerIp(),
                    serverInfo.getServerPort()
            );

            if (serverInfo.getPrivateKey() != null) {
                // 使用私钥认证
                jsch.addIdentity("server_key", serverInfo.getPrivateKey().getBytes(), null, null);
            } else {
                // 使用密码认证
                session.setPassword(AESUtil.decrypt(serverInfo.getPassword(), AESUtil.SERVER_AES_KEY));
            }

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect(5000);
            session.disconnect();
            return true;
        } catch (Exception e) {
            log.error("测试服务器连接失败: " + e.getMessage());
            throw new BusinessException("测试服务器连接失败！" + e.getMessage());
        }
    }
} 