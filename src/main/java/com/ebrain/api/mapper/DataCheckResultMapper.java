package com.ebrain.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ebrain.api.entities.DataCheckResult;
import com.ebrain.api.entities.DataCheckResultWithBLOBs;

public interface DataCheckResultMapper {
	int deleteByPrimaryKey(String tid);

    int insert(DataCheckResultWithBLOBs record);

    int insertSelective(DataCheckResultWithBLOBs record);

    DataCheckResultWithBLOBs selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(DataCheckResultWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(DataCheckResultWithBLOBs record);

    int updateByPrimaryKey(DataCheckResult record);

    DataCheckResultWithBLOBs selectByJobId(@Param("projectId") String projectId,@Param("jobId") String jobId, @Param("jobSequence")int jobSequence);

	List<DataCheckResult> list(String jobId);

	void delete(@Param("projectId") String projectId, @Param("jobId") String jobId, @Param("jobSequence") Integer sequence);
	
}