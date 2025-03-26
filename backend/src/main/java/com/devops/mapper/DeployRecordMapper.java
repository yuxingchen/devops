package com.devops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.entity.DeployRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeployRecordMapper extends BaseMapper<DeployRecord> {
    
    @Select("SELECT * FROM deploy_record WHERE env_id = #{envId} AND deleted = 0 ORDER BY created_time DESC")
    List<DeployRecord> findByEnvId(Long envId);
    
    @Select("SELECT * FROM deploy_record WHERE version_id = #{versionId} AND deleted = 0 ORDER BY created_time DESC")
    List<DeployRecord> findByVersionId(Long versionId);
} 