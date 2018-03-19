package com.ebrain.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.ModelInfo;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.ModelInfoMapper;
import com.ebrain.api.util.SysConfig;

@Service
public class ModelService {
	private Logger logger = LoggerFactory.getLogger(ModelService.class);
	@Autowired
	private ModelInfoMapper modelInfoMapper;
	
	
	public ModelInfo create(String userId, ModelInfo record)throws BaseException {
		try {
			record.setTid(SysConfig.getCreateId());
			record.setUpdateBy(userId);
			record.setCreateTime(SysConfig.getCurTime());
			modelInfoMapper.insertSelective(record);
			return record;
		} catch (Exception e) {
			logger.error("数据库操作错误，{}",e.getMessage());
			throw new BaseException(1,"数据库操作错误");
		}
	}


	
}
