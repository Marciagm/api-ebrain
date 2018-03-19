package com.ebrain.api.mapper;

import com.ebrain.api.entities.ModelInfo;

public interface ModelInfoMapper {
    int deleteByPrimaryKey(String tid);

    int insert(ModelInfo record);

    int insertSelective(ModelInfo record);

    ModelInfo selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(ModelInfo record);

    int updateByPrimaryKey(ModelInfo record);
}