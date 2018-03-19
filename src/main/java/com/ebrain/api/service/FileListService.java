/**
 * 已上传的文件列表服务
 */
package com.ebrain.api.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.FileList;
import com.ebrain.api.entities.ModelJobWithBLOBs;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.FileListMapper;
import com.ebrain.api.mapper.ModelJobMapper;
import com.ebrain.api.util.FilePretreatment;
import com.ebrain.api.util.SysConfig;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class FileListService {
	private Logger logger = LoggerFactory.getLogger(FileListService.class);
	@Autowired
	private FileListMapper fileListMapper;
	
	@Autowired
	private ModelJobMapper modelJobMapper;
	/**
	 * 文件列表
	 * @param filename
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws BaseException
	 */
	public PageInfo<FileList> list(String filename,String userId,int pageNum,int pageSize) throws BaseException{
		Page<FileList> page = PageHelper.startPage(pageNum, pageSize);
		try {
			if(!StringUtils.isEmpty(filename)){
				filename= "%"+filename+"%";
			}
			fileListMapper.list(filename,userId);
			return page.toPageInfo();
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}
	
	/**
	 * 增加文件
	 * @param fileList
	 * @return
	 * @throws BaseException
	 */
	public FileList create(FileList fileList) throws BaseException{
		fileList.setTid(SysConfig.getCreateId());
		fileList.setCreateTime(SysConfig.getCurTime());
		fileList.setUpdateBy(fileList.getCreateBy());
		fileList.setUpdateTime(fileList.getCreateTime());
		try {
			JSONObject json = FilePretreatment.run(fileList.getFilepath());
			json.put("dataSeparator",",");
			fileList.setDataResult(json.toString());
			
			fileListMapper.insertSelective(fileList);
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
		return fileList;
	}
	
	
	
	/**
	 * 删除文件
	 * @param userId
	 * @param tid
	 * @throws BaseException
	 */
	public void delete(String userId,String tid) throws BaseException{
		FileList fileList =fileListMapper.selectByPrimaryKey(tid);
		if(fileList==null){
			throw new BaseException(1, "文件不存在");
		}
		fileList.setStatus("deleted");
		fileList.setUpdateBy(userId);
		fileList.setUpdateTime(SysConfig.getCurTime());
		try {
			fileListMapper.updateByPrimaryKeySelective(fileList);
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
		
	}
	
	/**
	 * 查询文件
	 * @param tid
	 * @return
	 * @throws BaseException
	 */
	public FileList detail(String fileId) throws BaseException{
		try {
			return fileListMapper.selectByPrimaryKey(fileId);
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 查看原始文件数据
	 * @param jobId
	 * @return
	 * @throws BaseException
	 */
	public FileList getFileData(String jobId) throws BaseException{
		try {
			ModelJobWithBLOBs modelJob = modelJobMapper.selectByPrimaryKey(jobId);
			if(modelJob==null){
				throw new BaseException(1,"job不存在");
			}
			if(StringUtils.isEmpty(modelJob.getFileList())){
				throw new BaseException(1,"文件不存在");
			}
			return fileListMapper.selectByPrimaryKey(modelJob.getFileList().split(",")[0]);
		} catch (Exception e) {
			logger.error("数据库操作异常:{}", e.getMessage());
			throw new BaseException(1, "数据库操作异常");
		}
	}

	/**
	 * 验证文件格式
	 * @param fileIds
	 * @return
	 * @throws BaseException
	 */
	public void validate(String jobId) throws BaseException{
		try {
			
			if(StringUtils.isEmpty(jobId)){
				throw new BaseException(1,"缺少参数");
			}
			ModelJobWithBLOBs job = modelJobMapper.selectByPrimaryKey(jobId);
			if(job==null){
				throw new BaseException(2,"任务不存在");
			}
			if(StringUtils.isEmpty(job.getFileList())){
				return;
			}
			
			List<FileList> list = fileListMapper.selectByIds(job.getFileList().split(","));
			String line1="";
			String line2="";
			for(int i=0;i<list.size();i++){
				FileList file = list.get(i);
				line1 = readLine(file.getFilepath());
				for(int j =0;j<list.size();j++){
					if(i==j){
						continue;
					}
					FileList file2 = list.get(j);
					if(file.getTid().equals(file2.getTid())){
						continue;
					}
					if(file.getFilename().equals(file2.getFilename())){
						throw new BaseException(2,"文件【"+file.getFilename()+"】已存在！");
					}
					line2 = readLine(file.getFilepath());
					if(line1.equals(line2)){
						throw new BaseException(1,"文件格式不一致");
					}
					
				}
			}
			
		}catch(BaseException eb){
			throw eb;
		} catch (IOException e) {
			logger.error("文件读取异常:{}", e.getMessage());
			throw new BaseException(1, "文件读取异常");
		}
	}
	
	private String readLine(String file) throws IOException{
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		br.close();
		fr.close();
		return line;
	}
}
