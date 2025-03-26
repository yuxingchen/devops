package com.devops.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 服务器集群视图对象
 */
@Data
public class ServerClusterVO {
    
    private Long id;
    
    /**
     * 集群名称
     */
    @NotBlank(message = "集群名称不能为空")
    private String clusterName;
    
    /**
     * 集群编码
     */
    @NotBlank(message = "集群编码不能为空")
    private String clusterCode;
    
    /**
     * 集群IP
     */
    private String clusterIp;
    
    /**
     * 集群描述
     */
    private String description;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedTime;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 服务器ID列表
     */
    private List<Long> serverIds;
} 