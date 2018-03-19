package com.ebrain.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ebrain.api.entities.PredictResult;

public interface PredictResultMapper {
	int deleteByPrimaryKey(String tid);

    int insert(PredictResult record);

    int insertSelective(PredictResult record);

    PredictResult selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(PredictResult record);

    int updateByPrimaryKeyWithBLOBs(PredictResult record);

    int updateByPrimaryKey(PredictResult record);

	List<PredictResult> list(String jobId);

	PredictResult selectByJobId(@Param("projectId")String projectId,@Param("jobId")String jobId,@Param("sequence") int sequence,@Param("modelName")String modelName);

	List<PredictResult> history(PredictResult predictResult);

	List<PredictResult> historyList(@Param("projectId")String projectId, @Param("jobId")String jobId, @Param("jobSequence")int sequence, @Param("modelName")String modelName);
}