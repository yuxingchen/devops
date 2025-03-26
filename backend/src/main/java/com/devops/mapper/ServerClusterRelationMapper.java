package com.devops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.entity.ServerClusterRelation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务器集群关联Mapper接口
 */
@Mapper
public interface ServerClusterRelationMapper extends BaseMapper<ServerClusterRelation> {
    
    /**
     * 根据集群ID物理删除关联关系
     *
     * @param clusterId 集群ID
     * @return 影响行数
     */
    @Delete("DELETE FROM server_cluster_relation WHERE cluster_id = #{clusterId}")
    int deleteByClusterId(Long clusterId);
} 