/**
 * 数据预处理结果相关业务封装
 */
package com.ebrain.api.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.DataCheckResult;
import com.ebrain.api.entities.DataCheckResultWithBLOBs;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.DataCheckResultMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class DataCheckResultService {

	private Logger logger = LoggerFactory.getLogger(DataCheckResultService.class);
	@Autowired
	private DataCheckResultMapper dataCheckResultMapper;


	/**
	 * 查看
	 * @param jobId
	 * @param sequence
	 * @return
	 * @throws BaseException
	 */
	public DataCheckResultWithBLOBs detail(String projectId,String jobId,int sequence) throws BaseException {
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId不能为空");
			}
			return dataCheckResultMapper.selectByJobId(projectId,jobId,sequence);
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
	public PageInfo<DataCheckResult> list(String jobId,int pageNum,int pageSize) throws BaseException {
		Page<DataCheckResult> page = PageHelper.startPage(pageNum, pageSize);
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId参数不能为空");
			}
			dataCheckResultMapper.list(jobId);
			return page.toPageInfo();
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	public DataCheckResultWithBLOBs detail(String tid) throws BaseException {
		try {
			if(StringUtils.isEmpty(tid)){
				throw new BaseException(1, "jobId参数不能为空");
			}
			return dataCheckResultMapper.selectByPrimaryKey(tid);
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}
	
}
