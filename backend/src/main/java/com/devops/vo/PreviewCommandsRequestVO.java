package com.devops.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 预览部署命令请求VO
 *
 * @author yux
 */
@Data
public class PreviewCommandsRequestVO {

    /**
     * 应用ID
     */
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    /**
     * 版本ID
     */
    @NotNull(message = "版本ID不能为空")
    private Long versionId;

    /**
     * 服务器ID列表，逗号分隔
     */
    @NotNull(message = "服务器ID不能为空")
    private String serverIds;

    /**
     * 是否备份，true-备份，false-不备份
     */
    @NotNull(message = "是否备份不能为空")
    private Boolean backup;

    /**
     * 备份类型 full-全量备份，incremental-增量备份
     */
    @NotNull(message = "备份类型不能为空")
    private String backupType;

    /**
     * 部署命令
     */
    private String deployCommand;

} 