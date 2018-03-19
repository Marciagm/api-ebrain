/**
 * 预测结果数据相关业务实现
 */
package com.ebrain.api.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.PredictDetail;
import com.ebrain.api.entities.PredictResult;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.PredictDetailMapper;
import com.ebrain.api.mapper.PredictResultMapper;
import com.ebrain.api.util.FilePretreatment;
import com.ebrain.api.util.SysConfig;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class PredictResultServcie {

	private Logger logger = LoggerFactory.getLogger(PredictResultServcie.class);
	@Autowired
	private PredictResultMapper predictResultMapper;
	@Autowired
	private PredictDetailMapper predictDetailMapper;

	
	/**
	 * 保存预测信息
	 * @param userId
	 * @param record
	 * @return
	 * @throws BaseException
	 */
	public PredictResult create(String userId, PredictResult record) throws BaseException {
		try {
			record.setTid(SysConfig.getCreateId());
			record.setCreateTime(SysConfig.getCurTime());
			record.setStatus("waiting");
			predictResultMapper.insertSelective(record);
			
			PredictDetail detail = new PredictDetail();
			detail.setTid(record.getTid());
			detail.setJobId(record.getJobId());
			detail.setProjectId(record.getProjectId());
			detail.setJobSequence(record.getJobSequence());
			detail.setCreateTime(SysConfig.getCurTime());
			predictDetailMapper.insertSelective(detail);
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
	public PredictResult update(String userId, PredictResult record) throws BaseException {
		try {
			predictResultMapper.updateByPrimaryKeySelective(record);
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
	public PredictResult detail(String projectId ,String jobId,int sequence,String modelName) throws BaseException {
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId参数不能为空");
			}
			PredictResult result= predictResultMapper.selectByJobId(projectId,jobId,sequence,modelName);
			if(result==null){
				int retry=3;
				while(true){
					if(retry==0){
						throw new BaseException(2000,"预测结果还未生成");
					}
					retry--;
					Thread.sleep(2000l);
					result= predictResultMapper.selectByJobId(projectId,jobId,sequence,modelName);
					
				}
			}
			if(StringUtils.isEmpty(result.getPredictResult())){
				JSONObject json = FilePretreatment.parsePredictResultFile(result.getPredictFile(), "#|,",":\\d{1,}","",false);
				result.setPredictResult(json.toString());
				predictResultMapper.updateByPrimaryKeySelective(result);
			}
			return result;
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "文件解析异常");
		}
	}

	public PredictResult detail(String tid) throws BaseException {
		try {
			if(StringUtils.isEmpty(tid)){
				throw new BaseException(1, "tid参数不能为空");
			}
			PredictResult result= predictResultMapper.selectByPrimaryKey(tid);
			if(result==null){
				int retry=3;
				while(true){
					if(retry==0){
						throw new BaseException(1,"预测结果还未生成");
					}
					retry--;
					Thread.sleep(2000l);
					result= predictResultMapper.selectByPrimaryKey(tid);
					
				}
			}
			
			if(!"success".equals(result.getStatus())){
				throw new BaseException(1,"预测未完成");
			}
			if(StringUtils.isEmpty(result.getPredictResult())){
				JSONObject json = FilePretreatment.parsePredictResultFile(result.getPredictFile(), "#|,",":\\d{1,}","",false);
				result.setPredictResult(json.toString());
				predictResultMapper.updateByPrimaryKeySelective(result);
			}
			return result;
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "文件解析异常");
		}
	}
	/**
	 * 列表
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws BaseException
	 */
	public PageInfo<PredictResult> list(String jobId,int pageNum,int pageSize) throws BaseException {
		Page<PredictResult> page = PageHelper.startPage(pageNum, pageSize);
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId参数不能为空");
			}
			predictResultMapper.list(jobId);
			return page.toPageInfo();
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 预测历史
	 * @param projectId
	 * @param jobId
	 * @param sequence
	 * @param modelName
	 * @return
	 * @throws BaseException
	 */
	public List<PredictResult> predictHistory(String projectId,String jobId, int sequence,String modelName) throws BaseException {
		try {
			return predictResultMapper.historyList(projectId,jobId,sequence,modelName);
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}
}
