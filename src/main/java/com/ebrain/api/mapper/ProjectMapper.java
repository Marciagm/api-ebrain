package com.ebrain.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ebrain.api.entities.Project;

public interface ProjectMapper {
    int deleteByPrimaryKey(String tid);

    int insert(Project record);

    int insertSelective(Project record);

    Project selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(Project record);

    int updateByPrimaryKey(Project record);

	List<Project> list(@Param("userId")String userId);
}