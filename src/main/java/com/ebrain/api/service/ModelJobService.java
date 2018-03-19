/**
 * job相关业务实现
 */
package com.ebrain.api.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebrain.api.entities.FileList;
import com.ebrain.api.entities.ModelJob;
import com.ebrain.api.entities.ModelJobWithBLOBs;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.DataCheckResultMapper;
import com.ebrain.api.mapper.FileListMapper;
import com.ebrain.api.mapper.ModelJobMapper;
import com.ebrain.api.util.SysConfig;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ModelJobService {
	private Logger logger = LoggerFactory.getLogger(ModelJobService.class);
	@Autowired
	private ModelJobMapper modelJobMapper;

	@Autowired
	private DataCheckResultMapper dataCheckResultMapper;
	
	@Autowired
	private AlgorithmService algorithmService;
	
	@Autowired
	private FileListMapper fileListMapper;
	
	/**
	 * 新建job
	 * @param userId
	 * @param fileList
	 * @return
	 * @throws BaseException
	 */
	@Transactional(rollbackFor=Exception.class)
	public ModelJob create(String userId,String projectId, String fileId) throws BaseException {
		try {
			FileList file = fileListMapper.selectByPrimaryKey(fileId);
			ModelJobWithBLOBs modelJob = modelJobMapper.selectMaxSequenceJob(projectId);
			if(modelJob==null){
				modelJob = new ModelJobWithBLOBs();
				modelJob.setSequence(1);
			}else{
				modelJob.setSequence(modelJob.getSequence()+1);
			}
			modelJob.setProjectId(projectId);
			modelJob.setTid(SysConfig.getCreateId());
			modelJob.setTime(System.currentTimeMillis());
			modelJob.setJobStatus("running");
			modelJob.setReason("");
			modelJobMapper.insertSelective(modelJob);
			
			AlgorithmParam algorithmParam = new AlgorithmParam();
			algorithmParam.setJobId(modelJob.getTid());
			algorithmParam.setProjectId(projectId);
			algorithmParam.setSeparator(",");
			algorithmParam.setSequence(modelJob.getSequence());
			algorithmParam.setFiles(file.getFilepath());
			algorithmParam.setTime(System.currentTimeMillis());
			algorithmService.start(algorithmParam);
			return modelJob;
		} catch(BaseException e){
			throw e;
		}catch (Exception e) {
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
	public ModelJobWithBLOBs update(String userId, ModelJobWithBLOBs record) throws BaseException {
		try {
			modelJobMapper.updateByPrimaryKeySelective(record);
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
	public ModelJobWithBLOBs detail(String jobId,int sequence) throws BaseException {
		try {
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1, "jobId不能为空");
			}
			return modelJobMapper.selectByPrimaryKey(jobId);
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 列表查看
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws BaseException
	 */
	public PageInfo<ModelJob> list(String projectId,int pageNum,int pageSize) throws BaseException {
		Page<ModelJob> page = PageHelper.startPage(pageNum, pageSize);
		try {
			if(StringUtils.isEmpty(projectId)){
				throw new BaseException(1, "projectId参数不能为空");
			}
			modelJobMapper.list(projectId);
			return page.toPageInfo();
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	public List<ModelJob> getJobList(String projectId) {
		return modelJobMapper.list(projectId);
	}

	/**
	 * 运营版使用
	 * @param userId
	 * @param projectId
	 * @param fileIds
	 * @return
	 * @throws BaseException
	 */
	public ModelJob newJob(String userId, String projectId, List<String> fileIds) throws BaseException {
		try {
			ModelJobWithBLOBs modelJob = modelJobMapper.selectMaxSequenceJob(projectId);
			if(modelJob==null){
				modelJob = new ModelJobWithBLOBs();
				modelJob.setSequence(1);
			}else{
				modelJob.setSequence(modelJob.getSequence()+1);
			}
			modelJob.setProjectId(projectId);
			modelJob.setTid(SysConfig.getCreateId());
			modelJob.setTime(System.currentTimeMillis());
			modelJob.setJobStatus("running");
			modelJob.setReason("");
			modelJob.setFileList(String.join(",", fileIds));
			modelJobMapper.insertSelective(modelJob);
		
			return modelJob;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}
	
	/**
	 * 更新job文件 运营版使用
	 * @param userId
	 * @param projectId
	 * @param fileIds
	 * @return
	 * @throws BaseException
	 */
	public ModelJob updateJobFile(String jobId,String fileIds) throws BaseException {
		try {
			ModelJobWithBLOBs modelJob = modelJobMapper.selectByPrimaryKey(jobId);
			if(modelJob==null){
				throw new BaseException(1,"job不存在");
			}
			modelJob.setFileList(fileIds);
			modelJobMapper.updateByPrimaryKeySelective(modelJob);
			return modelJob;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	public ModelJob getJobStatus(String jobId) throws BaseException {
		try {
			ModelJobWithBLOBs modelJob = modelJobMapper.selectByPrimaryKey(jobId);
			if(modelJob==null){
				throw new BaseException(1,"job不存在");
			}
			return modelJob;
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}
	
	/**
	 * 删除任务文件
	 * @param userId
	 * @param jobId
	 * @param fileId
	 * @throws BaseException
	 */
	public void deleteJobFile(String userId, String jobId, String fileId) throws BaseException {
		try {
			ModelJobWithBLOBs modelJob = modelJobMapper.selectByPrimaryKey(jobId);
			if(modelJob==null){
				throw new BaseException(1,"job不存在");
			}
			if(StringUtils.isEmpty(modelJob.getFileList().trim())){
				return;
			}
			String[] ss = modelJob.getFileList().split(",");
			List<String> list = new ArrayList<String>();
			for(String s: ss){
				if(s.equals(fileId)){
					continue;
				}
				list.add(s);
			}
			String newFileList = String.join(",", list);
			modelJob.setFileList(newFileList);
			modelJob.setReason("");
			modelJob.setProgress("0");
			modelJob.setJobStatus("running");
			modelJobMapper.updateByPrimaryKeySelective(modelJob);
			dataCheckResultMapper.delete(modelJob.getProjectId(),modelJob.getTid(),modelJob.getSequence());
		} catch(BaseException eb){
			throw eb;
		}catch (Exception e) {
			logger.error("数据库操作错误，{}",e.getMessage());
			throw new BaseException(1,"数据库操作错误");
		}
	}

}
