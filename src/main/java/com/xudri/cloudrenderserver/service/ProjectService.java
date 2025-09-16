package com.xudri.cloudrenderserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xudri.cloudrenderserver.entity.Project;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * (Project)表服务接口
 *
 * @author maxyun
 * @since 2024-04-05 05:26:47
 */
public interface ProjectService extends IService<Project> {

    Map<String, Object> addProject(MultipartFile file) throws IOException;
}

