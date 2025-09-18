package com.xudri.cloudrenderserver.application.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xudri.cloudrenderserver.application.service.ProjectService;
import com.xudri.cloudrenderserver.application.service.SystemConfigService;
import com.xudri.cloudrenderserver.domain.entity.Project;
import com.xudri.cloudrenderserver.domain.entity.SystemConfig;
import com.xudri.cloudrenderserver.infrastructure.repository.ProjectDao;
import jakarta.annotation.Resource;
import lowentry.ue4.library.LowEntry;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * (Project)表服务实现类
 *
 * @author maxyun
 * @since 2024-04-05 05:26:49
 */
@Service("projectService")
public class ProjectServiceImpl extends ServiceImpl<ProjectDao, Project> implements ProjectService {

    private static final String RESULT = "result";
    private static final String RESULT_MSG = "resultMsg";

    @Resource
    private SystemConfigService systemConfigService;

    @Override
    public Map<String, Object> addProject(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();

        byte[] fileBytes = file.getBytes();
        String projectString = LowEntry.bytesToStringUtf8(fileBytes);

        // 保存文件到磁盘
        File destFile;
        try {
            destFile = saveFileToDisk(file);
        } catch (IOException e) {
            result.put(RESULT, false);
            result.put(RESULT_MSG, "文件上传失败,请确认项目是否已启动!");
            return result;
        }

        // 校验 JSON 内容
        if (!JSON.isValid(projectString)) {
            result.put(RESULT, false);
            result.put(RESULT_MSG, "文件内容不是有效的 JSON");
            return result;
        }

        JSONObject data = JSON.parseObject(projectString);
        String projectId = data.getString("ProjectID");
        if (projectId == null) {
            result.put(RESULT, false);
            result.put(RESULT_MSG, "JSON 缺少 ProjectID 字段");
            return result;
        }

        // 校验是否重复
        if (isProjectExist(projectId)) {
            result.put(RESULT, false);
            result.put(RESULT_MSG, "重复添加");
            return result;
        }

        // 新增项目
        Project project = buildProjectFromJson(data, fileBytes, destFile);
        result.put(RESULT, this.save(project));

        return result;
    }

    private File saveFileToDisk(MultipartFile file) throws IOException {
        SystemConfig systemConfig = systemConfigService.getById(1);
        String fileSavePath = systemConfig.getFilesavepath();

        File destFile = new File(fileSavePath, Objects.requireNonNull(file.getOriginalFilename()));
        ensureDirectoryExists(destFile.getParentFile());

        file.transferTo(destFile);
        return destFile;
    }

    private void ensureDirectoryExists(File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("无法创建目录: " + directory.getAbsolutePath());
        }
    }

    private boolean isProjectExist(String projectId) {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getProjectId, projectId);
        return this.count(queryWrapper) > 0;
    }

    private Project buildProjectFromJson(JSONObject data, byte[] fileBytes, File destFile) {
        Project project = new Project();
        project.setProjectId(data.getString("ProjectID"));
        project.setProjectName(data.getString("ProjectName"));
        project.setCreateDate(data.getString("CreateDate"));
        project.setProjectContent(LowEntry.bytesToHex(fileBytes));
        project.setSavePath(destFile.getPath().replace("\\", "/"));
        return project;
    }
}
