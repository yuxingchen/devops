package com.devops.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.common.BusinessException;
import com.devops.entity.Application;
import com.devops.mapper.ApplicationMapper;
import com.devops.service.ApplicationService;
import com.devops.service.ServerClusterService;
import com.devops.vo.ServerClusterVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 应用服务实现类
 *
 * @author yux
 */
@RequiredArgsConstructor
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    private final ServerClusterService serverClusterService;

    /**
     * 获取应用详情
     *
     * @param id 应用ID
     * @return 应用详情
     */
    @Override
    public Application getDetail(Long id) {
        Application application = getById(id);
        if (application == null) {
            throw new BusinessException("应用不存在");
        }
        // 获取应用关联的服务器ID列表
        Set<String> serverList = new HashSet<>();
        if (StringUtils.isNotEmpty(application.getServerIds())) {
            serverList.addAll(Arrays.asList(application.getServerIds().split(",")));
        }
        // 获取应用关联的集群服务器
        if (StringUtils.isNotBlank(application.getServerClusterIds())) {
            String[] clusterIds = application.getServerClusterIds().split(",");
            for (String clusterId : clusterIds) {
                ServerClusterVO clusterDetail = serverClusterService.getServerClusterDetail(Long.valueOf(clusterId));
                if (clusterDetail == null) {
                    continue;
                }
                List<Long> serverIds = clusterDetail.getServerIds();
                if (CollectionUtils.isEmpty(serverIds)) {
                    continue;
                }
                //  添加集群服务器ID
                serverIds.forEach(serverId -> serverList.add(serverId.toString()));
            }
        }
        // 设置关联服务器数组
        Long[] servers = serverList.stream().map(Long::parseLong).toArray(Long[]::new);
        application.setServers(servers);
        return application;
    }
}