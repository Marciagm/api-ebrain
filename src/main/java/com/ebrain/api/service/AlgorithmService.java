/**
 * 调用算法脚本的服务封装
 */
package com.ebrain.api.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebrain.api.entities.FileList;
import com.ebrain.api.entities.ModelJobWithBLOBs;
import com.ebrain.api.entities.TrainedResult;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.FileListMapper;
import com.ebrain.api.mapper.ModelJobMapper;
import com.ebrain.api.mapper.TrainedResultMapper;
import com.ebrain.api.util.JsonUtil;
import com.ebrain.api.util.SysConfig;
import com.google.gson.JsonObject;
/**
 * 调用linux脚本和启动算法
 * @author cheny
 *
 */
@Service
public class AlgorithmService {
	private Logger logger = LoggerFactory.getLogger(AlgorithmService.class);
	@Autowired
	private ModelJobMapper modelJobMapper;
	
	@Autowired
	private TrainedResultMapper trainedResultMapper;
	
	@Autowired
	private FileListMapper fileListMapper;
	/**
	 * 启动算法
	 * @param fileList
	 * @return
	 */
	public void start(AlgorithmParam algorithmParam) throws BaseException{
		logger.debug("call shell");
		
		/*String shellpath=SysConfig.getProperty("ebrain[train][algorithmshell]", "");   //程序路径

	    Process process =null;

	    String command1 = "chmod 755 " + shellpath;
	    try {
			process = Runtime.getRuntime().exec(command1);
			process.waitFor();

		    String var="--project "+algorithmParam.getProjectId();   
		    var +=" --job "+algorithmParam.getJobId();
		    var +=" --sequence "+algorithmParam.getSequence();
		    var +=" --time "+algorithmParam.getTime();
		    var +=" --files "+algorithmParam.getFiles();
		    var +=" --separator "+algorithmParam.getSeparator();
		    String command2 = "/bin/sh " + shellpath + " --algo_command job_all " + var; 
		    logger.debug("命令执行：{}",command2);
		    Runtime.getRuntime().exec(command2).waitFor();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"算法启动失败");
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new BaseException(2,"算法启动失败");
		}
	    */
	}
	
	/**
	 * 结束算法
	 * @param algorithmParam
	 * @throws BaseException
	 */
	public void stop(AlgorithmParam algorithmParam) throws BaseException{
		String shellpath=SysConfig.getProperty("ebrain[train][algorithmshell]", "");   //程序路径

	    Process process =null;

	    String command1 = "chmod 755 " + shellpath;
	    try {
			process = Runtime.getRuntime().exec(command1);
			process.waitFor();

			String var="--project "+algorithmParam.getProjectId();   
		    var +=" --job "+algorithmParam.getJobId();
		    var +=" --sequence "+algorithmParam.getSequence();             //参数
		    String command2 = "/bin/sh " + shellpath + " --algo_command kill" + var; 
		    Runtime.getRuntime().exec(command2).waitFor();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"结束算法启动失败");
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new BaseException(2,"结束算法启动失败");
		}
	    
	}
	
	
	/**
	 * 调用模型预测
	 * @param algorithmParam
	 * @throws BaseException
	 */
	public void runPredict(AlgorithmParam algorithmParam) throws BaseException{
		logger.debug("call shell");
		String shellpath=SysConfig.getProperty("ebrain[train][algorithmshell]", "");   //程序路径

	    Process process =null;

	    String command1 = "chmod 755 " + shellpath;
	    try {
			process = Runtime.getRuntime().exec(command1);
			process.waitFor();

		    String var="--project "+algorithmParam.getProjectId();   
		    var +=" --job "+algorithmParam.getJobId();
		    var +=" --sequence "+algorithmParam.getSequence();
		    var +=" --model "+algorithmParam.getModelName();
		    var +=" --time "+SysConfig.getCurTime();
		    var +=" --files "+algorithmParam.getFiles();
		    var +=" --separator "+algorithmParam.getSeparator();
		    String command2 = "/bin/sh " + shellpath + " --algo_command predict " + var; 
		    logger.debug("命令执行：{}",command2);
		    Runtime.getRuntime().exec(command2).waitFor();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"算法启动失败");
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new BaseException(2,"算法启动失败");
		}
	    
	}

	public void runStep(AlgorithmParam algorithmParam) throws BaseException{
		logger.debug("call runStep "+algorithmParam.getStep());
		String shellpath=SysConfig.getProperty("ebrain[train][algorithmshell]", "");   //程序路径

	    Process process =null;

	    String command1 = "chmod 755 " + shellpath;
	    try {
	    	process = Runtime.getRuntime().exec(command1);
			process.waitFor();
			String var="--project "+algorithmParam.getProjectId();   
		    var +=" --job "+algorithmParam.getJobId();
		    var +=" --sequence "+algorithmParam.getSequence();
		    var +=" --time "+SysConfig.getCurTime();
		    if(StringUtils.isEmpty(algorithmParam.getSeparator())){
		    	algorithmParam.setSeparator(",");
		    }
		    String cmd="";
		    var +=" --separator "+algorithmParam.getSeparator();
	    	if(algorithmParam.getStep().equals("dataCheck")){
	    		cmd="feature_analyse";
	    		ModelJobWithBLOBs job= modelJobMapper.selectByPrimaryKey(algorithmParam.getJobId());
	    		if(job==null || StringUtils.isEmpty(job.getFileList())){
	    			throw new BaseException(1,"请先上传文件");
	    		}
	    		String[] ids= job.getFileList().split(",");
	    		List<FileList> list = fileListMapper.selectByIds(ids);
	    		if(list==null || list.isEmpty()){
	    			throw new BaseException(2,"请先上传文件");
	    		}
	    		String file="";
	    		for(FileList fl :list){
	    			file+=fl.getFilepath()+",";
	    		}
	    		if(StringUtils.isEmpty(file)){
	    			throw new BaseException(3,"请先上传文件");
	    		}
	    		
	    		var +=" --files "+file;
	    	}else if(algorithmParam.getStep().equals("train")){
	    		 cmd="train";
	    		 var +=" --target "+algorithmParam.getLabelIndex();//序号
	    		 //var +=" --labelIndex "+algorithmParam.getLabelIndex();
	    	}else if(algorithmParam.getStep().equals("predict")){
	    		cmd="predict";
	    		List<TrainedResult> results = trainedResultMapper.selectByJobId(algorithmParam.getProjectId(),algorithmParam.getJobId(),algorithmParam.getSequence());
	    		if(results.size()==0){
	    			throw new BaseException(2,"模型训练结果不存在");
	    		}
	    		JsonObject json = JsonUtil.toJsonObject(results.get(0).getTrainedResult());
	    		
	    		var +=" --tid "+algorithmParam.getTid();//目标列序号
	    		var +=" --target "+json.get("target").getAsInt();//目标列序号
	    		var +=" --model "+algorithmParam.getModelName();
	    		var +=" --files "+algorithmParam.getFilePath();
	    	}else if(algorithmParam.getStep().equals("predict_explain")){
	    		cmd="predict_explain";
	    		var +=" --model "+algorithmParam.getModelPath();
	    		var +=" --files "+algorithmParam.getFiles();
	    	}else if(algorithmParam.getStep().equals("kill")){
	    		cmd="kill";
	    	}else{
	    		cmd="job_all";
	    		var +=" --model "+algorithmParam.getModelPath();
	    		var +=" --files "+algorithmParam.getFiles();
	    		var +=" --target "+algorithmParam.getLabelIndex();//序号
	    	}
		    
		    String command2 = "/bin/sh " + shellpath + " --algo_command "+cmd+" " + var; 
		    logger.debug("命令执行：{}",command2);
		    Runtime.getRuntime().exec(command2).waitFor();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new BaseException(1,"算法启动失败");
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new BaseException(2,"算法启动失败");
		}
	}
	

}
