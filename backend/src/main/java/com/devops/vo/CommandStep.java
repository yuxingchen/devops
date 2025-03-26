package com.devops.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 命令步骤
 *
 * @author yux
 */
@Data
@AllArgsConstructor
public class CommandStep {
    /**
     * 执行顺序
     */
    private Integer order;

    /**
     * 命令用途
     */
    private String purpose;

    /**
     * 具体命令
     */
    private String command;

    /**
     * 命令类型 shell:普通命令  java:程序
     */
    private String type;
}