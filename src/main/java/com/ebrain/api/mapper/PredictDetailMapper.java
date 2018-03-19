package com.ebrain.api.mapper;

import org.apache.ibatis.annotations.Param;

import com.ebrain.api.entities.PredictDetail;
import com.ebrain.api.entities.PredictResult;

public interface PredictDetailMapper {
    int deleteByPrimaryKey(String tid);

    int insert(PredictDetail record);

    int insertSelective(PredictDetail record);

    PredictDetail selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(PredictDetail record);

    int updateByPrimaryKeyWithBLOBs(PredictDetail record);

    int updateByPrimaryKey(PredictDetail record);

    PredictDetail selectByJobId(@Param("projectId")String projectId,@Param("jobId")String jobId,@Param("sequence") int sequence);
}