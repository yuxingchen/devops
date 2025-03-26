package com.devops.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.devops.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yux
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_info")
public class Application extends BaseEntity {

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用端口
     */
    private String appPort;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 部署路径
     */
    private String deployPath;

    /**
     * 备份路径
     */
    private String backupPath;

    /**
     * 关联服务器ID列表，逗号分隔
     */
    private String serverIds;

    /**
     * 关联服务器集群ID
     */
    private String serverClusterIds;

    /**
     * 关联服务器，非数据库字段
     */
    @TableField(exist = false)
    private Long[] servers;
} 