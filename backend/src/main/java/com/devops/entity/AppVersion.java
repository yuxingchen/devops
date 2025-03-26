package com.devops.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.devops.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_version")
public class AppVersion extends BaseEntity {
    
    /**
     * 关联的应用ID
     */
    private Long appId;
    
    /**
     * 关联的应用信息（非数据库字段）
     */
    @TableField(exist = false)
    private Application application;
    
    /**
     * 版本名称
     */
    private String versionName;
    
    /**
     * 版本号
     */
    private String versionCode;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 文件MD5
     */
    private String fileMd5;
    
    /**
     * 版本描述
     */
    private String description;

} 