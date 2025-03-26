package com.devops.controller;

import com.devops.common.Result;
import com.devops.entity.DeployEnv;
import com.devops.service.DeployEnvService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部署环境管理控制器
 * 提供环境的增删改查等基础操作
 *
 * @author yux
 */
@RestController
@RequestMapping("/api/envs")
@RequiredArgsConstructor
public class DeployEnvController {

    private final DeployEnvService deployEnvService;

    /**
     * 获取所有部署环境列表
     *
     * @return 环境列表
     */
    @GetMapping
    public Result<List<DeployEnv>> list() {
        return Result.success(deployEnvService.list());
    }

    /**
     * 根据ID获取部署环境详情
     *
     * @param id 环境ID
     * @return 环境详情
     */
    @GetMapping("/{id}")
    public Result<DeployEnv> getById(@PathVariable Long id) {
        return Result.success(deployEnvService.getById(id));
    }

    /**
     * 创建新的部署环境
     *
     * @param deployEnv 环境信息
     * @return 创建后的环境信息
     */
    @PostMapping
    public Result<DeployEnv> create(@RequestBody DeployEnv deployEnv) {
        deployEnvService.save(deployEnv);
        return Result.success(deployEnv);
    }

    /**
     * 更新部署环境信息
     *
     * @param id        环境ID
     * @param deployEnv 更新的环境信息
     * @return 更新后的环境信息
     */
    @PutMapping("/{id}")
    public Result<DeployEnv> update(@PathVariable Long id, @RequestBody DeployEnv deployEnv) {
        deployEnv.setId(id);
        deployEnvService.updateById(deployEnv);
        return Result.success(deployEnv);
    }

    /**
     * 删除部署环境
     *
     * @param id 环境ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deployEnvService.removeById(id);
        return Result.success();
    }
}