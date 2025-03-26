package com.devops.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.devops.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务器集群关联实体类
 *
 * @author yux
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("server_cluster_relation")
public class ServerClusterRelation extends BaseEntity {

    /**
     * 集群ID
     */
    private Long clusterId;

    /**
     * 服务器ID
     */
    private Long serverId;

} 