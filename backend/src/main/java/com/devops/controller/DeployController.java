package com.devops.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.devops.common.Result;
import com.devops.entity.DeployLog;
import com.devops.entity.DeployRecord;
import com.devops.service.DeployService;
import com.devops.vo.DeployCommandVO;
import com.devops.vo.DeployRecordVO;
import com.devops.vo.DeployRequestVO;
import com.devops.vo.PreviewCommandsRequestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 部署管理控制器
 * 提供应用部署、回滚、部署记录查询等功能
 *
 * @author yux
 */
@RestController
@RequestMapping("/api/deploy")
@RequiredArgsConstructor
public class DeployController {

    private final DeployService deployService;

    /**
     * 预览部署命令
     *
     * @param request 预览请求参数
     * @return 部署命令列表
     */
    @PostMapping("/preview-commands")
    public Result<List<DeployCommandVO>> previewCommands(@RequestBody @Valid PreviewCommandsRequestVO request) {
        return Result.success(deployService.previewDeployCommands(
                request.getAppId(),
                request.getVersionId(),
                request.getServerIds(),
                request.getBackup(),
                request.getBackupType(),
                request.getDeployCommand()
        ));
    }

    /**
     * 执行部署操作
     *
     * @param request 部署请求参数
     * @return 部署记录
     */
    @PostMapping
    public Result<DeployRecord> deploy(@RequestBody @Valid DeployRequestVO request) {
        return Result.success(deployService.deploy(
                request.getAppId(),
                request.getVersionId(),
                request.getServerIds(),
                request.getBackup(),
                request.getBackupType(),
                request.getDeployCommand()));
    }

    /**
     * 执行回滚操作
     *
     * @param recordId 部署记录ID
     * @return 回滚后的部署记录
     */
    @PostMapping("/{recordId}/rollback")
    public Result<DeployRecord> rollback(@PathVariable Long recordId) {
        return Result.success(deployService.rollback(recordId));
    }

    /**
     * 获取部署记录列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param appId    应用ID（可选）
     * @param serverId 服务器ID（可选）
     * @return 部署记录分页数据
     */
    @GetMapping("/records")
    public Result<IPage<DeployRecordVO>> listRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long appId,
            @RequestParam(required = false) Long serverId) {
        return Result.success(deployService.listRecords(pageNum, pageSize, appId, serverId));
    }

    /**
     * 获取部署日志
     *
     * @param recordId 部署记录ID
     * @return 部署日志列表
     */
    @GetMapping("/records/{recordId}/logs")
    public Result<List<DeployLog>> getDeployLogs(@PathVariable Long recordId) {
        return Result.success(deployService.getDeployLogs(recordId));
    }
} 