package com.devops.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 部署日志实体类
 */
@Data
public class DeployLog implements Serializable {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 部署记录ID
     */
    private Long deployId;
    
    /**
     * 服务器ID
     */
    private Long serverId;
    
    /**
     * 日志类型：0-系统日志，1-部署日志
     */
    private Integer logType;
    
    /**
     * 日志内容
     */
    private String logContent;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
} 