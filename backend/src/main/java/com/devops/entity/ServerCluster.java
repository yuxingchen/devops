package com.devops.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.devops.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务器集群实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("server_cluster")
public class ServerCluster extends BaseEntity {

    /**
     * 集群名称
     */
    private String clusterName;

    /**
     * 集群编码
     */
    private String clusterCode;

    /**
     * 集群IP
     */
    private String clusterIp;

    /**
     * 集群描述
     */
    private String description;
} 