package com.xudri.cloudrenderserver.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xudri.cloudrenderserver.api.Result;
import com.xudri.cloudrenderserver.entity.SystemConfig;
import com.xudri.cloudrenderserver.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName SystemInfoController
 * @Description TODO
 * @Author MaxYun
 * @Date 2024/4/8 10:10
 * @Version 1.0
 */

@RestController
@Tag(name = "系统管理")
@RequestMapping("/sysconfig")
public class SystemInfoController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("getConfig")
    @Operation(summary = "获取配置")
    public Result<SystemConfig> getConfig() {
        SystemConfig systemConfig = systemConfigService.getById(1);
        return Result.ok(systemConfig);
    }

    @PostMapping("updateConfig")
    @Operation(summary = "更新配置")
    public Result<Boolean> updateConfig(@RequestBody SystemConfig systemConfig) {
        UpdateWrapper<SystemConfig> updateWrapper = new UpdateWrapper<>();
        systemConfig.setId(1);
        updateWrapper.eq("id", 1);
        return Result.ok(systemConfigService.update(systemConfig, updateWrapper));
    }
}
