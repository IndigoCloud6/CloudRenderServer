//package com.xudri.cloudrenderserver.controller;
//
//import com.alibaba.fastjson2.JSONObject;
//import com.xudri.cloudrenderserver.api.Result;
//import com.xudri.cloudrenderserver.entity.Instance;
//import com.xudri.cloudrenderserver.service.InstanceService;
//import io.swagger.v3.oas.annotations.Operation;
//import jakarta.annotation.Resource;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.Serializable;
//
///**
// * (Instance)表控制层
// *
// * @author maxyun
// * @since 2024-05-13 11:15:47
// */
//@RestController
//@RequestMapping("instance")
//public class InstanceController {
//    /**
//     * 服务对象
//     */
//    @Resource
//    private InstanceService instanceService;
//
//    /**
//     * 分页查询所有数据
//     *
//     * @return 所有数据
//     */
//    @GetMapping
//    @Operation(summary = "查询所有实例")
//    public Result selectAll() {
//        return Result.ok(this.instanceService.getAllInstance());
//    }
//
//    /**
//     * 通过主键查询单条数据
//     *
//     * @param id 主键
//     * @return 单条数据
//     */
//    @GetMapping("{id}")
//    public Result selectOne(@PathVariable Serializable id) {
//        return Result.ok(this.instanceService.getById(id));
//    }
//
//    /**
//     * 新增数据
//     *
//     * @param instance 实体对象
//     * @return 新增结果
//     */
//    @PostMapping("add")
//    @Operation(summary = "新增实例")
//    public Result insert(@RequestBody JSONObject instance) {
//        if (instance.isEmpty()) {
//            return Result.failed("非法数据");
//        }
//        return Result.ok(this.instanceService.addInstance(instance));
//    }
//
//    /**
//     * 修改数据
//     *
//     * @param instance 实体对象
//     * @return 修改结果
//     */
//    @PutMapping
//    public Result update(@RequestBody Instance instance) {
//        return Result.ok(this.instanceService.updateById(instance));
//    }
//
//    /**
//     * 删除数据
//     *
//     * @param id 主键结合
//     * @return 删除结果
//     */
//    @DeleteMapping
//    public Result delete(@RequestParam("idList") String id) {
//        return Result.ok(this.instanceService.removeById(id));
//    }
//
//    @GetMapping("run")
//    @Operation(summary = "启动实例")
//    public Result run(@RequestParam("id") String id) throws InterruptedException {
//        instanceService.runInstance(id);
//        return Result.ok("已启动");
//    }
//
//    @GetMapping("kill")
//    @Operation(summary = "终止实例")
//    public Result kill(@RequestParam("id") String id) throws InterruptedException {
//        return Result.ok(instanceService.killInstance(id));
//    }
//
//    /**
//     * 更新数据
//     *
//     * @param instance 实体对象
//     * @return 新增结果
//     */
//    @PostMapping("update")
//    @Operation(summary = "更新实例")
//    public Result update(@RequestBody JSONObject instance) {
//        if (instance.isEmpty()) {
//            return Result.failed("非法数据");
//        }
//        return Result.ok(this.instanceService.aupdateInstance(instance));
//    }
//}
