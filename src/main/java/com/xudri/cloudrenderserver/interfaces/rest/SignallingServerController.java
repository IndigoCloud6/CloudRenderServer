package com.xudri.cloudrenderserver.interfaces.rest;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson2.JSONObject;
import com.xudri.cloudrenderserver.common.exception.Result;
import com.xudri.cloudrenderserver.domain.entity.SystemConfig;
import com.xudri.cloudrenderserver.infrastructure.network.SignallingServer;
import com.xudri.cloudrenderserver.application.service.ClientManagerService;
import com.xudri.cloudrenderserver.application.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/signallingserver")
@Tag(name = "信令服务器相关接口")
@RequiredArgsConstructor
public class SignallingServerController {


    private final SignallingServer signallingServer;
    private final ClientManagerService clientManagerService;
    private final SystemConfigService systemConfigService;


    @RequestMapping(value = "/run", method = RequestMethod.GET)
    public Result<String> run() {
        SystemConfig systemConfig = systemConfigService.getById(1);
        int signallingServerPort = systemConfig.getSignallingserverport();
        signallingServer.setServicePort(signallingServerPort);
        if (NetUtil.isUsableLocalPort(signallingServerPort)) {
            signallingServer.run();
            return Result.ok("启动成功！");
        } else {
            return Result.failed("启动失败,端口被占用    ！");
        }
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.GET)
    public Result<String> shutdown() {
        signallingServer.stop();
        return Result.ok("关闭成功！");
    }

    @Operation(summary = "信令服务状态")
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public Result status() {
        return Result.ok(signallingServer.isRunning());
    }

    @RequestMapping(value = "/getport", method = RequestMethod.GET)
    public Result getPort() {
        int port = signallingServer.getServicePort();
        JSONObject result = new JSONObject();
        result.put("port", port);
        return Result.ok(result);
    }

    @Operation(summary = "信令服务端口设置")
    @RequestMapping(value = "/setPort", method = RequestMethod.GET)
    public Result setPort(@Parameter(description = "端口", required = true, example = "9999") int port) {
        signallingServer.setServicePort(port);
        return Result.ok("");
    }

    @Operation(summary = "连接详情")
    @RequestMapping(value = "/getDetail", method = RequestMethod.GET)
    public Result getDetail() {
        return Result.ok(clientManagerService.getDetails());
    }
}
