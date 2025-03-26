package com.devops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.entity.ServerCluster;
import com.devops.entity.ServerClusterRelation;
import com.devops.mapper.ServerClusterMapper;
import com.devops.mapper.ServerClusterRelationMapper;
import com.devops.service.ServerClusterService;
import com.devops.vo.ServerClusterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务器集群服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServerClusterServiceImpl extends ServiceImpl<ServerClusterMapper, ServerCluster> implements ServerClusterService {

    @Resource
    private ServerClusterRelationMapper serverClusterRelationMapper;

    /**
     * 创建服务器集群
     *
     * @param serverClusterVO 服务器集群VO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createServerCluster(ServerClusterVO serverClusterVO) {
        // 转换为实体对象
        ServerCluster serverCluster = new ServerCluster();
        BeanUtils.copyProperties(serverClusterVO, serverCluster);

        // 保存集群信息
        boolean success = save(serverCluster);
        if (success && serverClusterVO.getServerIds() != null && !serverClusterVO.getServerIds().isEmpty()) {
            // 保存集群与服务器的关联关系
            List<ServerClusterRelation> relations = serverClusterVO.getServerIds().stream()
                    .map(serverId -> {
                        ServerClusterRelation relation = new ServerClusterRelation();
                        relation.setClusterId(serverCluster.getId());
                        relation.setServerId(serverId);
                        return relation;
                    })
                    .collect(Collectors.toList());
            for (ServerClusterRelation relation : relations) {
                serverClusterRelationMapper.insert(relation);
            }
        }
        return success;
    }

    /**
     * 更新服务器集群
     *
     * @param serverClusterVO 服务器集群VO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateServerCluster(ServerClusterVO serverClusterVO) {
        // 转换为实体对象
        ServerCluster serverCluster = new ServerCluster();
        BeanUtils.copyProperties(serverClusterVO, serverCluster);

        // 更新集群信息
        boolean success = updateById(serverCluster);
        if (success) {
            try {
                // 使用自定义SQL物理删除所有关联关系
                serverClusterRelationMapper.deleteByClusterId(serverCluster.getId());
                
                // 创建新的关联关系
                if (serverClusterVO.getServerIds() != null && !serverClusterVO.getServerIds().isEmpty()) {
                    List<ServerClusterRelation> relations = new ArrayList<>();
                    for (Long serverId : serverClusterVO.getServerIds()) {
                        ServerClusterRelation relation = new ServerClusterRelation();
                        relation.setClusterId(serverCluster.getId());
                        relation.setServerId(serverId);
                        relations.add(relation);
                    }
                    // 批量插入新的关联关系
                    for (ServerClusterRelation relation : relations) {
                        serverClusterRelationMapper.insert(relation);
                    }
                }
            } catch (Exception e) {
                log.error("更新集群关联关系失败", e);
                throw new RuntimeException("更新集群关联关系失败", e);
            }
        }
        return success;
    }

    /**
     * 删除服务器集群
     *
     * @param id 集群ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteServerCluster(Long id) {
        // 删除集群信息
        boolean success = removeById(id);
        if (success) {
            // 删除关联关系
            LambdaQueryWrapper<ServerClusterRelation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ServerClusterRelation::getClusterId, id);
            serverClusterRelationMapper.delete(wrapper);
        }
        return success;
    }

    /**
     * 获取服务器集群详情
     *
     * @param id 集群ID
     * @return 服务器集群VO
     */
    @Override
    public ServerClusterVO getServerClusterDetail(Long id) {
        // 获取集群信息
        ServerCluster serverCluster = getById(id);
        if (serverCluster == null) {
            return null;
        }

        // 转换为视图对象
        ServerClusterVO serverClusterVO = new ServerClusterVO();
        BeanUtils.copyProperties(serverCluster, serverClusterVO);

        // 获取关联的服务器ID列表
        LambdaQueryWrapper<ServerClusterRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServerClusterRelation::getClusterId, id);
        List<ServerClusterRelation> relations = serverClusterRelationMapper.selectList(wrapper);
        List<Long> serverIds = relations.stream()
                .map(ServerClusterRelation::getServerId)
                .collect(Collectors.toList());
        serverClusterVO.setServerIds(serverIds);

        return serverClusterVO;
    }

    /**
     * 分页查询服务器集群
     *
     * @param page 分页对象
     * @param clusterName 集群名称
     * @param clusterCode 集群编码
     * @return 分页结果
     */
    @Override
    public Page<ServerClusterVO> pageServerClusters(Page<ServerCluster> page, String clusterName, String clusterCode) {
        // 构建查询条件
        LambdaQueryWrapper<ServerCluster> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(clusterName), ServerCluster::getClusterName, clusterName)
                .like(StringUtils.isNotBlank(clusterCode), ServerCluster::getClusterCode, clusterCode)
                .orderByDesc(ServerCluster::getCreatedTime);

        // 执行分页查询
        Page<ServerCluster> resultPage = page(page, wrapper);

        // 转换为视图对象
        Page<ServerClusterVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage, "records");

        List<ServerClusterVO> voList = new ArrayList<>();
        for (ServerCluster serverCluster : resultPage.getRecords()) {
            ServerClusterVO vo = new ServerClusterVO();
            BeanUtils.copyProperties(serverCluster, vo);

            // 获取关联的服务器ID列表
            LambdaQueryWrapper<ServerClusterRelation> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.eq(ServerClusterRelation::getClusterId, serverCluster.getId());
            List<ServerClusterRelation> relations = serverClusterRelationMapper.selectList(relationWrapper);
            List<Long> serverIds = relations.stream()
                    .map(ServerClusterRelation::getServerId)
                    .collect(Collectors.toList());
            vo.setServerIds(serverIds);

            voList.add(vo);
        }
        voPage.setRecords(voList);

        return voPage;
    }
} 