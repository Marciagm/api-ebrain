/**
 * 模型训练相关业务实现
 */
package com.ebrain.api.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.TrainedResult;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.TrainedResultMapper;
import com.ebrain.api.util.SysConfig;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class TrainedResultService {

	private Logger logger = LoggerFactory.getLogger(TrainedResultService.class);
	@Autowired
	private TrainedResultMapper trainedResultMapper;

	/**
	 * 保存训练结果
	 * @param userId
	 * @param record
	 * @return
	 * @throws BaseException
	 */
	public TrainedResult create(String userId, TrainedResult record) throws BaseException {
		try {
			record.setJobId(SysConfig.getCreateId());
			record.setJobSequence(0);
			trainedResultMapper.insertSelective(record);
			return record;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 更新
	 * @param userId
	 * @param record
	 * @return
	 * @throws BaseException
	 */
	public TrainedResult update(String userId, TrainedResult record) throws BaseException {
		try {
			trainedResultMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
		return null;
	}

	/**
	 * 查看
	 * @param jobId
	 * @param sequence
	 * @return
	 * @throws BaseException
	 */
	public List<TrainedResult> detail(String projectId,String jobId,int sequence) throws BaseException {
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId参数不能为空");
			}
			List<TrainedResult> list = trainedResultMapper.selectByJobId(projectId,jobId,sequence);
			if(list==null){
				int retry=3;
				while(true){
					if(retry==0){
						throw new BaseException(1,"结果还未生成");
					}
					retry--;
					Thread.sleep(2000l);
					list= trainedResultMapper.selectByJobId(projectId,jobId,sequence);
				}
			}
			return list;
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 列表
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws BaseException
	 */
	public PageInfo<TrainedResult> list(String jobId,int pageNum,int pageSize) throws BaseException {
		Page<TrainedResult> page = PageHelper.startPage(pageNum, pageSize);
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId参数不能为空");
			}
			trainedResultMapper.list(jobId,pageNum,pageSize);
			return page.toPageInfo();
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

}
