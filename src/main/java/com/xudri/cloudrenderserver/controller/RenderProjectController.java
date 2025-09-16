package com.xudri.cloudrenderserver.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xudri.cloudrenderserver.api.Result;
import com.xudri.cloudrenderserver.entity.Project;
import com.xudri.cloudrenderserver.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName RenderProjectController
 * @Description TODO
 * @Author MaxYun
 * @Date 2024/4/3 19:00
 * @Version 1.0
 */


@RestController
@RequestMapping("/project")
@Tag(name = "可视化编辑器工程文件")
@Transactional
public class RenderProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/upload")
    @Operation(summary = "上传工程文件")
    public Result<Object> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.failed("File is empty");
        }
        String extName = FileNameUtil.extName(file.getOriginalFilename());
        if (!"dtproject".equals(extName)) {
            return Result.failed("UnSupport File");
        }
        //byte[] fileBytes = file.getBytes();
        return Result.ok(projectService.addProject(file));
    }

    @GetMapping("/list")
    @Operation(summary = "工程列表")
    public Result listProject(@RequestParam(required = false) String name) {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.select(Project::getProjectId, Project::getProjectName, Project::getSavePath, Project::getCreateDate);
        queryWrapper.like(StringUtils.isNotBlank(name), Project::getProjectName, name);
        return Result.ok(projectService.list(queryWrapper));
    }

    @GetMapping("/remove")
    @Operation(summary = "删除工程")
    public Result removeProject(@RequestParam("id") String id) {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Project::getProjectId, id);
        return Result.ok(projectService.remove(queryWrapper));
    }
}
