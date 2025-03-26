package com.devops.vo;

import lombok.Data;

import java.util.List;

/**
 * 部署命令VO
 *
 * @author yux
 */
@Data
public class DeployCommandVO {

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 命令列表
     */
    private List<CommandStep> commands;

} 