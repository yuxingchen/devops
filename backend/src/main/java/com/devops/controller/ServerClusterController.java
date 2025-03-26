package com.devops.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devops.common.Result;
import com.devops.entity.ServerCluster;
import com.devops.service.ServerClusterService;
import com.devops.vo.ServerClusterVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 服务器集群控制器
 */
@RestController
@RequestMapping("/api/server-clusters")
public class ServerClusterController {
    
    @Resource
    private ServerClusterService serverClusterService;
    
    /**
     * 创建服务器集群
     */
    @PostMapping
    public Result<Boolean> createServerCluster(@RequestBody @Valid ServerClusterVO serverClusterVO) {
        boolean success = serverClusterService.createServerCluster(serverClusterVO);
        return Result.success(success);
    }
    
    /**
     * 更新服务器集群
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateServerCluster(@PathVariable Long id, @RequestBody @Valid ServerClusterVO serverClusterVO) {
        serverClusterVO.setId(id);
        boolean success = serverClusterService.updateServerCluster(serverClusterVO);
        return Result.success(success);
    }
    
    /**
     * 删除服务器集群
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteServerCluster(@PathVariable Long id) {
        boolean success = serverClusterService.deleteServerCluster(id);
        return Result.success(success);
    }
    
    /**
     * 获取服务器集群详情
     */
    @GetMapping("/{id}")
    public Result<ServerClusterVO> getServerClusterDetail(@PathVariable Long id) {
        ServerClusterVO serverClusterVO = serverClusterService.getServerClusterDetail(id);
        return Result.success(serverClusterVO);
    }
    
    /**
     * 分页查询服务器集群列表
     */
    @GetMapping("/page")
    public Result<Page<ServerClusterVO>> pageServerClusters(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String clusterName,
            @RequestParam(required = false) String clusterCode) {
        Page<ServerCluster> page = new Page<>(current, size);
        Page<ServerClusterVO> result = serverClusterService.pageServerClusters(page, clusterName, clusterCode);
        return Result.success(result);
    }
} 