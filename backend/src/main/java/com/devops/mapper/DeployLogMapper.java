package com.devops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.entity.DeployLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeployLogMapper extends BaseMapper<DeployLog> {
    
    @Select("SELECT * FROM deploy_log WHERE deploy_id = #{deployId} ORDER BY created_time ASC")
    List<DeployLog> findByDeployId(Long deployId);
    
    @Select("SELECT * FROM deploy_log WHERE deploy_id = #{deployId} AND server_id = #{serverId} ORDER BY created_time ASC")
    List<DeployLog> findByDeployIdAndServerId(Long deployId, Long serverId);
} 