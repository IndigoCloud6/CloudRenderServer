//package com.xudri.cloudrenderserver.controller;
//
//import com.xudri.cloudrenderserver.api.Result;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @ClassName ProcessManagerController
// * @Description TODO
// * @Author MaxYun
// * @Date 2024/3/20 10:18
// * @Version 1.0
// */
//@RestController
//@RequestMapping("/process")
//@Tag(name = "进程管理")
//public class ProcessManagerController {
//
//    @PostMapping("/startTurn")
//    @Operation(summary = "启动转发服务")
//    public Result startCoturn(){
//        CoturnStartupParam coturnStartupParam = new CoturnStartupParam();
//        coturnStartupParam.setLocalIp("172.16.128.244");
//        coturnStartupParam.setSoftPath("./bin/coturn/turnserver.exe");
//        coturnStartupParam.setTurnPort("19306");
//        coturnStartupParam.setRealm("PixelStreaming");
//        coturnStartupParam.setPublicIP("103.84.217.28");
//        coturnStartupParam.setPidfilePath("./bin/coturn/coturn.pid");
//        coturnStartupParam.setTurnUsername("PixelStreamingUser");
//        coturnStartupParam.setTurnPassword("AnotherTURNintheroad");
//        coturnStartupParam.setConfigPath("./bin/coturn/turnserver.conf");
//        coturnStartupParam.setLogPath("./bin/coturn/turnserver.log");
//        String line = ConmandLineBuilder.coturnCommandLine(coturnStartupParam);
//        ProcessManager.run("",line);
//        return Result.ok("ok");
//    }
//
//    @PostMapping("/stopTurn")
//    @Operation(summary = "关闭转发服务")
//    public Result stopCoturn(){
//        ProcessManager.killProcessByPort(19306);
//        return Result.ok("ok");
//    }
//}
