package com.xudri.cloudrenderserver.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.xudri.cloudrenderserver.common.constant.Instance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Instance)表数据库访问层
 *
 * @author maxyun
 * @since 2024-05-13 11:58:39
 */
@Mapper
public interface InstanceDao extends BaseMapper<Instance> {

/**
* 批量新增数据（MyBatis原生foreach方法）
*
* @param entities List<Instance> 实例对象列表
* @return 影响行数
*/
int insertBatch(@Param("entities") List<Instance> entities);

/**
* 批量新增或按主键更新数据（MyBatis原生foreach方法）
*
* @param entities List<Instance> 实例对象列表
* @return 影响行数
* @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
*/
int insertOrUpdateBatch(@Param("entities") List<Instance> entities);

}

