package com.ebrain.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ebrain.api.entities.TrainedResult;

public interface TrainedResultMapper {
    int deleteByPrimaryKey(String tid);

    int insert(TrainedResult record);

    int insertSelective(TrainedResult record);

    TrainedResult selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(TrainedResult record);

    int updateByPrimaryKeyWithBLOBs(TrainedResult record);

    int updateByPrimaryKey(TrainedResult record);

    List<TrainedResult> list(String jobId, int pageNum, int pageSize);

    List<TrainedResult> selectByJobId(@Param("projectId")String projectId,@Param("jobId")String jobId,@Param("sequence") int sequence);
}