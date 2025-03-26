package com.devops.entity;

import com.devops.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServerInfo extends BaseEntity {

    /**
     * 环境ID，标识服务器所属的环境。
     */
    private Long envId;

    /**
     * 服务器名称，用于标识服务器的名称。
     */
    private String serverName;

    /**
     * 服务器IP地址，用于网络通信。
     */
    private String serverIp;

    /**
     * 服务器端口，用于网络通信。
     */
    private Integer serverPort;

    /**
     * 连接服务器的用户名。
     */
    private String username;

    /**
     * 连接服务器的密码。
     */
    private String password;

    /**
     * 连接服务器的私钥。
     */
    private String privateKey;

    /**
     * 用户在服务器上的工作路径。
     */
    private String userPath;
}