package com.xudri.cloudrenderserver.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.xudri.cloudrenderserver.domain.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Project)表数据库访问层
 *
 * @author maxyun
 * @since 2024-04-05 05:26:42
 */
@Mapper
public interface ProjectDao extends BaseMapper<Project> {

/**
* 批量新增数据（MyBatis原生foreach方法）
*
* @param entities List<Project> 实例对象列表
* @return 影响行数
*/
int insertBatch(@Param("entities") List<Project> entities);

/**
* 批量新增或按主键更新数据（MyBatis原生foreach方法）
*
* @param entities List<Project> 实例对象列表
* @return 影响行数
* @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
*/
int insertOrUpdateBatch(@Param("entities") List<Project> entities);

}

