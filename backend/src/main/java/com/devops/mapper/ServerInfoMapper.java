package com.devops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.entity.ServerInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServerInfoMapper extends BaseMapper<ServerInfo> {
    
    @Select("SELECT * FROM server_info WHERE env_id = #{envId} AND deleted = 0")
    List<ServerInfo> findByEnvId(Long envId);
} 