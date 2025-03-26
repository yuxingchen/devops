package com.devops.vo;

import com.devops.entity.DeployRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部署记录VO，包含额外的显示信息
 * @author yux
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeployRecordVO extends DeployRecord {
    
    /**
     * 应用名称
     */
    private String appName;
    
    /**
     * 版本名称
     */
    private String versionName;
    
    /**
     * 版本号
     */
    private String versionCode;
    
    /**
     * 服务器名称列表（逗号分隔）
     */
    private String serverNames;
    
    /**
     * 创建人名称
     */
    private String createdByName;
} 