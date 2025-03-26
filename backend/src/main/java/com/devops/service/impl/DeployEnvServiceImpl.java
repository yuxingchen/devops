package com.devops.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.entity.DeployEnv;
import com.devops.mapper.DeployEnvMapper;
import com.devops.service.DeployEnvService;
import org.springframework.stereotype.Service;

/**
 * 部署环境服务实现类
 *
 * @author yux
 */
@Service
public class DeployEnvServiceImpl extends ServiceImpl<DeployEnvMapper, DeployEnv> implements DeployEnvService {

}
