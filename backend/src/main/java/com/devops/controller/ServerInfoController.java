package com.devops.controller;

import com.devops.common.Result;
import com.devops.entity.ServerInfo;
import com.devops.service.ServerInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务器信息管理控制器
 * 提供服务器信息的增删改查和连接测试等功能
 *
 * @author yux
 */
@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerInfoController {

    private final ServerInfoService serverInfoService;

    /**
     * 获取服务器列表
     *
     * @param envId      环境ID
     * @param serverName 服务器名称（模糊查询）
     * @param serverIp   服务器IP（模糊查询）
     * @return 服务器列表
     */
    @GetMapping
    public Result<List<ServerInfo>> list(
            @RequestParam(required = false) Long envId,
            @RequestParam(required = false) String serverName,
            @RequestParam(required = false) String serverIp) {
        return Result.success(serverInfoService.listServers(envId, serverName, serverIp));
    }

    /**
     * 根据环境ID获取服务器列表
     *
     * @param envId 环境ID
     * @return 服务器列表
     */
    @GetMapping("/env/{envId}")
    public Result<List<ServerInfo>> listByEnvId(@PathVariable Long envId) {
        return Result.success(serverInfoService.listByEnvId(envId));
    }

    /**
     * 根据ID获取服务器详情
     *
     * @param id 服务器ID
     * @return 服务器详情
     */
    @GetMapping("/{id}")
    public Result<ServerInfo> getById(@PathVariable Long id) {
        return Result.success(serverInfoService.getById(id));
    }



    /**
     * 创建新的服务器信息
     *
     * @param serverInfo 服务器信息
     * @return 创建后的服务器信息
     */
    @PostMapping
    public Result<ServerInfo> create(@RequestBody ServerInfo serverInfo) {
        serverInfoService.save(serverInfo);
        return Result.success(serverInfo);
    }

    /**
     * 更新服务器信息
     *
     * @param id         服务器ID
     * @param serverInfo 更新的服务器信息
     * @return 更新后的服务器信息
     */
    @PutMapping("/{id}")
    public Result<ServerInfo> update(@PathVariable Long id, @RequestBody ServerInfo serverInfo) {
        serverInfo.setId(id);
        serverInfoService.updateById(serverInfo);
        return Result.success(serverInfo);
    }

    /**
     * 删除服务器信息
     *
     * @param id 服务器ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        serverInfoService.removeById(id);
        return Result.success();
    }

    /**
     * 测试服务器连接
     *
     * @param id 服务器ID
     * @return 连接测试结果
     */
    @PostMapping("/{id}/test")
    public Result<Boolean> testConnection(@PathVariable Long id) {
        return Result.success(serverInfoService.testConnection(id));
    }
} 