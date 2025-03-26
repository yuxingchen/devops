package com.devops.entity;

import com.devops.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeployEnv extends BaseEntity {
    
    /**
     * 环境名称，用于标识部署环境的名称。
     */
    private String envName;
    
    /**
     * 环境代码，用于唯一标识部署环境的代码。
     */
    private String envCode;
    
    /**
     * 环境描述，用于描述部署环境的详细信息。
     */
    private String description;
}