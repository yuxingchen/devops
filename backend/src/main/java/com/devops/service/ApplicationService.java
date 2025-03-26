package com.devops.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.entity.Application;

/**
 * 应用服务
 *
 * @author yux
 */
public interface ApplicationService extends IService<Application> {

    /**
     * 获取应用详情
     *
     * @param id 应用ID
     * @return 应用详情
     */
    Application getDetail(Long id);
}