package com.devops.entity;

import com.devops.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 部署记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeployRecord extends BaseEntity {
    
    /**
     * 应用ID
     */
    private Long appId;
    
    /**
     * 版本ID
     */
    private Long versionId;
    
    /**
     * 部署服务器ID列表，逗号分隔
     */
    private String serverIds;
    
    /**
     * 部署状态：0-部署中，1-成功，2-失败
     */
    private Integer deployStatus;
    
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
} 