package com.devops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.entity.ServerCluster;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务器集群Mapper接口
 */
@Mapper
public interface ServerClusterMapper extends BaseMapper<ServerCluster> {
} 