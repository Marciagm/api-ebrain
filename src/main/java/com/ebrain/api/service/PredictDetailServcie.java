/**
 * 预测数据相关业务实现
 */
package com.ebrain.api.service;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.PredictDetail;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.PredictDetailMapper;
import com.ebrain.api.util.FilePretreatment;

@Service
public class PredictDetailServcie {

	private Logger logger = LoggerFactory.getLogger(PredictDetailServcie.class);
	@Autowired
	private PredictDetailMapper predictDetailMapper;
	
	
	public PredictDetail detail(String projectId, String jobId, Integer sequence) throws BaseException {
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId参数不能为空");
			}
			PredictDetail result= predictDetailMapper.selectByJobId(projectId,jobId,sequence);
			if(result==null){
				throw new BaseException(100,"预测结果还未生成");
			}
			if(StringUtils.isEmpty(result.getDetail())){
				JSONObject json = FilePretreatment.parsePredictResultFile(result.getDetailFile(),"#|,",null,null,false);
				result.setDetail(json.toString());
				predictDetailMapper.updateByPrimaryKeySelective(result);
			}
			return result;
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "文件解析异常");
		}
	}
	
	
	public PredictDetail detail(String tid) throws BaseException {
		try {
			if(StringUtils.isEmpty(tid)){
				throw new BaseException(1, "tid参数不能为空");
			}
			PredictDetail result= predictDetailMapper.selectByPrimaryKey(tid);
			if(result==null){
				throw new BaseException(100,"预测结果还未生成");
			}
			if(StringUtils.isEmpty(result.getDetail())){
				JSONObject json = FilePretreatment.parsePredictResultFile(result.getDetailFile(),"#|,",null,null,false);
				result.setDetail(json.toString());
				predictDetailMapper.updateByPrimaryKeySelective(result);
			}
			return result;
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "文件解析异常");
		}
	}
	
	public static void main(String[] args) {
		try {
			JSONObject json = FilePretreatment.parsePredictResultFile("C:\\Users\\cheny\\Documents\\predict_ins.predict.new","#|,",null,null,false);
			System.out.println(json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
