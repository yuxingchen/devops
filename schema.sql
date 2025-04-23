-- Active: 1740469385739@@127.0.0.1@33060@deploy_platform
-- 创建数据库
CREATE DATABASE IF NOT EXISTS deploy_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE deploy_platform;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user
(
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username     VARCHAR(50)  NOT NULL COMMENT '用户名',
    password     VARCHAR(100) NOT NULL COMMENT '密码',
    real_name    VARCHAR(50) COMMENT '真实姓名',
    email        VARCHAR(100) COMMENT '邮箱',
    phone        VARCHAR(20) COMMENT '手机号',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_by   BIGINT       DEFAULT NULL COMMENT '创建人ID',
    created_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    updated_by   BIGINT       DEFAULT NULL COMMENT '更新人ID',
    updated_time DATETIME     DEFAULT NULL COMMENT  '更新时间',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role
(
    id           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name    VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code    VARCHAR(20) NOT NULL COMMENT '角色编码',
    status       TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description  VARCHAR(200) COMMENT '角色描述',
    created_by   BIGINT      NOT NULL COMMENT '创建人ID',
    created_time DATETIME    DEFAULT NULL COMMENT '创建时间',
    updated_by   BIGINT      DEFAULT NULL COMMENT '更新人ID',
    updated_time DATETIME    DEFAULT NULL COMMENT  '更新时间',
    deleted      TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色表';


CREATE TABLE IF NOT EXISTS sys_user_role
(
    id           BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id      BIGINT   NOT NULL COMMENT '用户ID',
    role_id      BIGINT   NOT NULL COMMENT '角色ID',
    created_by   BIGINT   NOT NULL COMMENT '创建人ID',
    created_time DATETIME DEFAULT NULL COMMENT '创建时间',
    updated_by   BIGINT   DEFAULT NULL COMMENT '更新人ID',
    updated_time DATETIME DEFAULT NULL COMMENT  '更新时间',
    deleted      TINYINT  NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户角色关联表';

-- 部署环境表
CREATE TABLE IF NOT EXISTS deploy_env
(
    id           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    env_name     VARCHAR(50) NOT NULL COMMENT '环境名称',
    env_code     VARCHAR(20) NOT NULL COMMENT '环境编码',
    description  VARCHAR(200) COMMENT '环境描述',
    created_by   BIGINT      NOT NULL COMMENT '创建人ID',
    created_time DATETIME    DEFAULT NULL COMMENT '创建时间',
    updated_by   BIGINT      DEFAULT NULL COMMENT '更新人ID',
    updated_time DATETIME    DEFAULT NULL COMMENT  '更新时间',
    deleted      TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_env_code (env_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='部署环境表';

-- 服务器信息表
CREATE TABLE IF NOT EXISTS server_info
(
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    env_id       BIGINT       NOT NULL COMMENT '环境ID',
    server_name  VARCHAR(50)  NOT NULL COMMENT '服务器名称',
    server_ip    VARCHAR(50)  NOT NULL COMMENT '服务器IP',
    server_port  INT          NOT NULL COMMENT '服务器SSH端口',
    username     VARCHAR(50)  NOT NULL COMMENT '登录用户名',
    password     VARCHAR(200) COMMENT '登录密码',
    private_key  TEXT COMMENT '私钥内容',
    user_path  VARCHAR(200) NOT NULL COMMENT '用户路径',
    created_by   BIGINT       NOT NULL COMMENT '创建人ID',
    created_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    updated_by   BIGINT       DEFAULT NULL COMMENT '更新人ID',
    updated_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='服务器信息表';

-- 应用版本表
CREATE TABLE IF NOT EXISTS app_version
(
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    app_id       bigint(20)   NOT NULL COMMENT '应用ID',
    version_name VARCHAR(50)  NOT NULL COMMENT '版本名称',
    version_code VARCHAR(50)  NOT NULL COMMENT '版本号',
    file_name    VARCHAR(200) NOT NULL COMMENT '文件名称',
    file_path    VARCHAR(500) NOT NULL COMMENT 'MinIO中的文件路径',
    file_size    BIGINT       NOT NULL COMMENT '文件大小(字节)',
    file_md5     VARCHAR(32)  NOT NULL COMMENT '文件MD5值',
    description  VARCHAR(500) COMMENT '版本描述',
    created_by   BIGINT       NOT NULL COMMENT '创建人ID',
    created_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    updated_by   BIGINT       DEFAULT NULL COMMENT '更新人ID',
    updated_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_version_code (app_id, version_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='应用版本表';

-- 部署记录表
CREATE TABLE IF NOT EXISTS deploy_record
(
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    app_id        BIGINT       NOT NULL COMMENT '应用ID',
    version_id    BIGINT       NOT NULL COMMENT '版本ID',
    server_ids    VARCHAR(500) NOT NULL COMMENT '部署服务器ID列表，逗号分隔',
    deploy_status TINYINT      NOT NULL COMMENT '部署状态：0-部署中，1-成功，2-失败',
    start_time    DATETIME     NOT NULL COMMENT '开始时间',
    end_time      DATETIME COMMENT '结束时间',
    created_by    BIGINT       NOT NULL COMMENT '创建人ID',
    created_time  DATETIME     NOT NULL DEFAULT NULL COMMENT '创建时间',
    updated_by    BIGINT       DEFAULT NULL COMMENT '更新人ID',
    updated_time  DATETIME     DEFAULT NULL COMMENT '更新时间',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='部署记录表';

-- 部署日志表
CREATE TABLE IF NOT EXISTS deploy_log
(
    id           BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    deploy_id    BIGINT   NOT NULL COMMENT '部署记录ID',
    server_id    BIGINT   NOT NULL COMMENT '服务器ID',
    log_type     TINYINT  NOT NULL COMMENT '日志类型：0-系统日志，1-部署日志',
    log_content  TEXT     NOT NULL COMMENT '日志内容',
    created_time DATETIME NOT NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='部署日志表';

-- 创建应用信息表
CREATE TABLE IF NOT EXISTS `app_info`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `app_code`     varchar(50)  NOT NULL COMMENT '应用编码',
    `app_name`     varchar(100) NOT NULL COMMENT '应用名称',
    `app_port`     INT          NOT NULL COMMENT '应用端口',
    `description`  varchar(500) DEFAULT NULL COMMENT '应用描述',
    `deploy_path`  varchar(200) NOT NULL COMMENT '部署路径',
    `backup_path`  varchar(200) DEFAULT NULL COMMENT '备份路径',
    `server_ids`           varchar(500) DEFAULT NULL COMMENT '关联服务器ID列表，逗号分隔',
    `server_cluster_ids`   varchar(500) DEFAULT NULL COMMENT '关联服务器集群ID列表，逗号分隔',
    `created_by`           bigint(20)   DEFAULT NULL COMMENT '创建人',
    `created_time`         datetime     DEFAULT NULL COMMENT '创建时间',
    `updated_by`           bigint(20)   DEFAULT NULL COMMENT '更新人',
    `updated_time`         datetime     DEFAULT NULL COMMENT '更新时间',
    `deleted`      tinyint(1)   DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_code` (`app_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='应用信息表';

-- 创建应用版本表
CREATE TABLE IF NOT EXISTS `app_version`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `app_id`       bigint(20)   NOT NULL COMMENT '应用ID',
    `version_name` varchar(100) NOT NULL COMMENT '版本名称',
    `version_code` varchar(50)  NOT NULL COMMENT '版本号',
    `file_name`    varchar(200) NOT NULL COMMENT '文件名',
    `file_path`    varchar(500) NOT NULL COMMENT '文件路径',
    `file_size`    bigint(20)   DEFAULT NULL COMMENT '文件大小',
    `file_md5`     varchar(32)  DEFAULT NULL COMMENT '文件MD5',
    `description`  varchar(500) DEFAULT NULL COMMENT '版本描述',
    `created_by`   bigint(20)   DEFAULT NULL COMMENT '创建人',
    `created_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `updated_by`   bigint(20)   DEFAULT NULL COMMENT '更新人',
    `updated_time` datetime     DEFAULT NULL COMMENT '更新时间',
    `deleted`      tinyint(1)   DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_version` (`app_id`, `version_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='应用版本表';

-- 服务器集群表
CREATE TABLE IF NOT EXISTS server_cluster
(
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    cluster_name VARCHAR(50)  NOT NULL COMMENT '集群名称',
    cluster_code VARCHAR(50)  NOT NULL COMMENT '集群编码',
    cluster_ip   VARCHAR(50)  DEFAULT NULL COMMENT '集群IP',
    description  VARCHAR(500) COMMENT '集群描述',
    created_by   BIGINT       NOT NULL COMMENT '创建人ID',
    created_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    updated_by   BIGINT       DEFAULT NULL COMMENT '更新人ID',
    updated_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_cluster_code (cluster_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='服务器集群表';

-- 服务器集群关联表
CREATE TABLE IF NOT EXISTS server_cluster_relation
(
    id           BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    cluster_id   BIGINT   NOT NULL COMMENT '集群ID',
    server_id    BIGINT   NOT NULL COMMENT '服务器ID',
    created_by   BIGINT   NOT NULL COMMENT '创建人ID',
    created_time DATETIME DEFAULT NULL COMMENT '创建时间',
    updated_by   BIGINT   DEFAULT NULL COMMENT '更新人ID',
    updated_time DATETIME DEFAULT NULL COMMENT '更新时间',
    deleted      TINYINT  NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_cluster_server (cluster_id, server_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='服务器集群关联表';

insert into deploy_platform.sys_user_role (id, user_id, role_id, created_by, created_time, updated_by, updated_time, deleted) values (1, 1, 1, 0, '2025-02-14 09:22:39', 20250214092239, '2025-02-24 09:03:37', 0);
insert into deploy_platform.sys_user (id, username, password, real_name, email, phone, status, created_by, created_time, updated_by, updated_time, deleted) values (1, 'admin', '$2a$10$ED8CtLSb5YahKAk4rdwT/uu3XM/YK.66cvHkSHbrM1BHTfBaYL3JO', 'admin', null, null, 1, 0, '2025-02-14 07:35:26', null, '2025-02-14 07:46:57', 0);
insert into deploy_platform.sys_role (id, role_name, role_code, status, description, created_by, created_time, updated_by, updated_time, deleted) values (1, '管理员', 'ADMIN', 1, '管理员', 0, '2025-02-14 09:22:20', null, '2025-02-14 09:22:20', 0);
