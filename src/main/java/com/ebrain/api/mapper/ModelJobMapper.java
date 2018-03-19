package com.ebrain.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ebrain.api.entities.ModelJob;
import com.ebrain.api.entities.ModelJobWithBLOBs;

public interface ModelJobMapper {
    int deleteByPrimaryKey(String tid);

    int insert(ModelJobWithBLOBs record);

    int insertSelective(ModelJob record);

    ModelJobWithBLOBs selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(ModelJobWithBLOBs record);

    int updateByPrimaryKey(ModelJobWithBLOBs record);

	List<ModelJob> list(@Param("projectId")String projectId);
	
	ModelJobWithBLOBs selectMaxSequenceJob(String projectId);
}