package com.ebrain.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ebrain.api.entities.ModelExplain;

public interface ModelExplainMapper {
    int deleteByPrimaryKey(String tid);

    int insert(ModelExplain record);

    int insertSelective(ModelExplain record);

    ModelExplain selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(ModelExplain record);

    int updateByPrimaryKeyWithBLOBs(ModelExplain record);

    int updateByPrimaryKey(ModelExplain record);

	ModelExplain selectByJobId(@Param("projectId")String projectId,@Param("jobId")String jobId, @Param("sequence")int sequence);

	List<ModelExplain> list(@Param("jobId")String jobId);

	java.util.List<ModelExplain> selectByProjectId(String projectId);
}