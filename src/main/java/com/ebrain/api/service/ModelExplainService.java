/**
 * 模型解释相关业务实现
 */
package com.ebrain.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.ModelExplain;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.ModelExplainMapper;
import com.ebrain.api.util.JsonUtil;
import com.ebrain.api.util.SysConfig;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@Service
public class ModelExplainService {
	private Logger logger = LoggerFactory.getLogger(ModelExplainService.class);
	@Autowired
	private ModelExplainMapper modelExplainMapper;

	/**
	 * 保存模型解释信息
	 * @param userId
	 * @param record
	 * @return
	 * @throws BaseException
	 */
	public ModelExplain create(String userId, ModelExplain record) throws BaseException {
		try {
			record.setJobId(SysConfig.getCreateId());
			record.setJobSequence(0);
			modelExplainMapper.insertSelective(record);
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
	public ModelExplain update(String userId, ModelExplain record) throws BaseException {
		try {
			modelExplainMapper.updateByPrimaryKeySelective(record);
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
	public ModelExplain detail(String projectId,String jobId,int sequence) throws BaseException {
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId不能为空");
			}
			ModelExplain modelExplain = modelExplainMapper.selectByJobId(projectId,jobId,sequence);
			if(modelExplain==null){
				throw new BaseException(1,"模型解释数据未生成");
			}
			return calculatePrecise(modelExplain);
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
	public PageInfo<ModelExplain> list(String jobId,int pageNum,int pageSize) throws BaseException {
		Page<ModelExplain> page = PageHelper.startPage(pageNum, pageSize);
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId参数不能为空");
			}
			modelExplainMapper.list(jobId);
			return page.toPageInfo();
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 根据模型id查看模型解释详情
	 * @param modelId
	 * @throws BaseException
	 */
	public ModelExplain detail(String modelId)throws BaseException {
		try{
			ModelExplain modelExplain = modelExplainMapper.selectByPrimaryKey(modelId);
			if(modelExplain==null){
				throw new BaseException(1,"模型解释数据为生成");
			}
			return calculatePrecise(modelExplain);
		}catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}
	
	private ModelExplain calculatePrecise(ModelExplain modelExplain){
		//开始计算准确率
		JsonObject modelExplainJson = JsonUtil.toJsonObject(modelExplain.getModelExplain());
		JsonObject model = modelExplainJson.get("models").getAsJsonArray().get(0).getAsJsonObject();
        float threshold=model.get("precise_recall").getAsJsonObject().get("threshold").getAsFloat();
        JsonArray precise_recall=model.get("precise_recall").getAsJsonObject().get("x_y").getAsJsonArray();
        float dat=Float.MAX_VALUE;
        float dd=0;
        int index = 0;
        for(int i=0;i<precise_recall.size();i++){
        	dd = Math.abs(precise_recall.get(i).getAsJsonObject().get("pctr").getAsFloat()-threshold);
        	if(dd<dat){
        		dat = dd;
        		index = i;
        	}
        }
        threshold = precise_recall.get(index).getAsJsonObject().get("precise").getAsFloat();
        String precise =  String .format("%.2f",threshold*100);
        modelExplainJson.addProperty("precise", precise);
        modelExplain.setModelExplain(modelExplainJson.toString());
        //结束计算准确率
        return modelExplain;
	}

	
	/**
	 * 模型解释列表
	 * @param jobId
	 * @return
	 */
	public List<Map<String,Object>> list(String jobId) throws BaseException{
		try{
			List<ModelExplain> list = modelExplainMapper.list(jobId);
			List<Map<String,Object>> result = new ArrayList<Map<String,Object>>(list.size());
			for(ModelExplain me : list){
				Map<String,Object> map = new HashMap<String,Object>();
				JsonObject json = JsonUtil.toJsonObject(me.getModelExplain());
				map.put("projectId", me.getProjectId());
				map.put("jobId", me.getJobId());
				map.put("sequence", me.getJobSequence()+"");
				JsonArray array  = json.get("models").getAsJsonArray();
				
				String ss="";
				String trainTime="";
				for(int i=0;i<array.size();i++){
					JsonObject model = array.get(i).getAsJsonObject();
					//开始计算准确率
		            float threshold=model.get("precise_recall").getAsJsonObject().get("threshold").getAsFloat();
		            JsonArray precise_recall=model.get("precise_recall").getAsJsonObject().get("x_y").getAsJsonArray();
		            float dat=Float.MAX_VALUE;
		            float dd=0;
		            int index = 0;
		            for(int j=0;j<precise_recall.size();j++){
		            	dd = Math.abs(precise_recall.get(j).getAsJsonObject().get("pctr").getAsFloat()-threshold);
		            	if(dd<dat){
		            		dat = dd;
		            		index = j;
		            	}
		            }
		            threshold = precise_recall.get(index).getAsJsonObject().get("precise").getAsFloat();
		            String precise =  String .format("%.2f",threshold*100)+"%";
		            //结束计算准确率
		            model.addProperty("precise",precise);
		            model.addProperty("auc",model.get("roc").getAsJsonObject().get("auc").getAsString());
		            array.set(i, model);
					ss +=model.get("model_name").getAsString()+",";
					trainTime = model.get("train_time").getAsString();
				}
				map.put("models",array);
				map.put("algorithm",ss.length()>0? ss.substring(0,ss.length()-1) : "");
				map.put("trainTime",trainTime);
				
				
				
				result.add(map);
			}
			return result;
		}catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

}
