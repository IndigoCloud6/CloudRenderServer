package com.xudri.cloudrenderserver.application.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xudri.cloudrenderserver.infrastructure.repository.ProjectDao;
import com.xudri.cloudrenderserver.common.constant.Project;
import com.xudri.cloudrenderserver.common.constant.SystemConfig;
import com.xudri.cloudrenderserver.application.service.ProjectService;
import com.xudri.cloudrenderserver.application.service.SystemConfigService;
import lowentry.ue4.library.LowEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * (Project)表服务实现类
 *
 * @author maxyun
 * @since 2024-04-05 05:26:49
 */
@Service("projectService")
public class ProjectServiceImpl extends ServiceImpl<ProjectDao, Project> implements ProjectService {

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public Map<String, Object> addProject(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        Map<String, Object> result = new HashMap<>();
        //byte[] key = LowEntry.stringToBytesUtf8("maxyun@xudri");
        //byte[] decrypted = LowEntry.decryptAes(fileBytes, key, true);
        String projectString = LowEntry.bytesToStringUtf8(fileBytes);

        SystemConfig systemConfig = systemConfigService.getById(1);
        String filesavepath = systemConfig.getFilesavepath();
        File destFile = new File(filesavepath + File.separator + file.getOriginalFilename());
        // 确保目标文件的目录存在
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        // 将文件保存到目标位置
        try{
            file.transferTo(destFile);
        }catch (Exception e){
            result.put("result",false);
            result.put("resultMsg","文件上传失败,请确认项目是否已启动!");
            return result;
        }
        if (JSON.isValid(projectString)) {
            JSONObject data = JSON.parseObject(projectString);
            String projectId = data.getString("ProjectID");
            LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Project::getProjectId, projectId);
            long count = this.count(queryWrapper);
            if (count > 0) {
                result.put("result", false);
                result.put("resultMsg", "重复添加");
            } else {
                Project project = new Project();
                project.setProjectName(data.getString("ProjectName"));
                project.setProjectId(data.getString("ProjectID"));
                project.setCreateDate(data.getString("CreateDate"));
                project.setProjectContent(LowEntry.bytesToHex(fileBytes));
                project.setSavePath(destFile.getPath().replace("\\","/"));
                result.put("result", this.save(project));
            }
        }
        return result;
    }
}
