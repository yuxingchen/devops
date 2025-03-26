package com.devops.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.entity.ServerCluster;
import com.devops.vo.ServerClusterVO;

/**
 * 服务器集群服务接口
 *
 * @author yux
 */
public interface ServerClusterService extends IService<ServerCluster> {

    /**
     * 创建服务器集群
     *
     * @param serverClusterVO 服务器集群视图对象
     * @return 是否创建成功
     */
    boolean createServerCluster(ServerClusterVO serverClusterVO);

    /**
     * 更新服务器集群
     *
     * @param serverClusterVO 服务器集群视图对象
     * @return 是否更新成功
     */
    boolean updateServerCluster(ServerClusterVO serverClusterVO);

    /**
     * 删除服务器集群
     *
     * @param id 集群ID
     * @return 是否删除成功
     */
    boolean deleteServerCluster(Long id);

    /**
     * 获取服务器集群详情
     *
     * @param id 集群ID
     * @return 服务器集群视图对象
     */
    ServerClusterVO getServerClusterDetail(Long id);

    /**
     * 分页查询服务器集群列表
     *
     * @param page        分页对象
     * @param clusterName 集群名称
     * @param clusterCode 集群编码
     * @return 分页数据
     */
    Page<ServerClusterVO> pageServerClusters(Page<ServerCluster> page, String clusterName, String clusterCode);
} 