package com.devops.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.entity.AppVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AppVersionMapper extends BaseMapper<AppVersion> {
    
    @Select("SELECT * FROM app_version WHERE version_code = #{versionCode} AND deleted = 0 LIMIT 1")
    AppVersion findByVersionCode(String versionCode);
} 