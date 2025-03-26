package com.devops.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.devops.common.PageRequest;
import com.devops.common.Result;
import com.devops.entity.Application;
import com.devops.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apps")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * 分页查询应用列表
     */
    @GetMapping
    public Result<IPage<Application>> list(PageRequest pageRequest, String keyword) {
        Page<Application> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like(Application::getAppName, keyword).or().like(Application::getAppCode, keyword);
        }
        return Result.success(applicationService.page(page, queryWrapper));
    }

    /**
     * 获取应用详情
     */
    @GetMapping("/{id}")
    public Result<Application> get(@PathVariable Long id) {
        return Result.success(applicationService.getDetail(id));
    }

    /**
     * 创建应用
     */
    @PostMapping
    public Result<Boolean> create(@RequestBody Application application) {
        return Result.success(applicationService.save(application));
    }

    /**
     * 更新应用
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Application application) {
        application.setId(id);
        return Result.success(applicationService.updateById(application));
    }

    /**
     * 删除应用
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(applicationService.removeById(id));
    }
} 