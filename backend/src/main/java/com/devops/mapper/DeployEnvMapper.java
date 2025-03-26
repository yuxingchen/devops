package com.devops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.entity.DeployEnv;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeployEnvMapper extends BaseMapper<DeployEnv> {
} 