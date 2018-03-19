package com.ebrain.api.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebrain.api.entities.FileList;
import com.ebrain.api.entities.ModelExplain;
import com.ebrain.api.entities.ModelInfo;
import com.ebrain.api.entities.ModelJob;
import com.ebrain.api.entities.ModelJobWithBLOBs;
import com.ebrain.api.entities.PredictResult;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.mapper.FileListMapper;
import com.ebrain.api.service.LoginService;
import com.ebrain.api.service.ModelExplainService;
import com.ebrain.api.service.ModelJobService;
import com.ebrain.api.service.ModelService;
import com.ebrain.api.service.PredictResultServcie;
import com.ebrain.api.util.FailureReturn;
import com.ebrain.api.util.JsonUtil;
import com.ebrain.api.util.SuccessReturn;
import com.github.pagehelper.StringUtil;
import com.google.gson.JsonArray;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path("/model")
@Api(value = "/model")
public class ModelEndpoint {
	private Logger logger = LoggerFactory.getLogger(ModelEndpoint.class);
	@Autowired
	private ModelExplainService modelExplainService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private ModelJobService modelJobService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private FileListMapper fileListMapper;
	
	@Autowired
	private PredictResultServcie predictResultServcie;
	
	@GET
	@Path("explain/{jobId}")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "查看模型解释", notes = "查看模型解释")
	public Response explain(@HeaderParam("Authorization") String token,
			@PathParam("jobId") String jobId){
		try {
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	ModelExplain modelExplain  = modelExplainService.detail(jobId);
            return Response.status(200).entity(SuccessReturn.build(modelExplain)).build();
        } catch (BaseException e) {
            logger.error("查看模型解释【/model/explain/{}】,err:{}",jobId , e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("查看模型解释【/model/explain/{}】,err:{}",jobId,e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
        }
	}
	
	/**
	 * 运营版新建job，此时不需要启动算法
	 * 
	 * @param projectId
	 * @param token
	 * @param modelJob
	 * @return
	 */
	@POST
	@Path("new-job/{projectId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response newJob(@PathParam("projectId") String projectId,List<String> fileIds,
			@HeaderParam("Authorization") String token) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			String userId = loginService.getLoginUserId(token);
			ModelJob modelJob = modelJobService.newJob(userId, projectId,fileIds);
			return Response.status(200).entity(SuccessReturn.build(modelJob)).build();
		} catch (BaseException e) {
			logger.error("新建job【/project/new-job】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("新建job【/project/new-job】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "操作失败")).build();
		}
	}
	
	/**
	 * 运营版本查询job信息
	 * @param jobId
	 * @param token
	 * @param sequence
	 * @return
	 */
	@GET
	@Path("jobFiles/{jobId}/{sequence}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getJobFiles(@PathParam("jobId") String jobId,
			@HeaderParam("Authorization") String token,
			@PathParam("sequence") int sequence) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.isLogin(token);
			if (StringUtil.isEmpty(jobId)) {
				throw new BaseException(1, "jobId不能为空");
			}
			
			ModelJobWithBLOBs modelJob = modelJobService.detail(jobId,sequence);
			String[] ids=null;
			try{
				JsonArray array = JsonUtil.toJsonArray(modelJob.getFileList());
				ids =new String[array.size()];
				for(int i=0;i<ids.length;i++){
					ids[i] = array.get(i).getAsString();
				}
			}catch(Exception e){
				ids = modelJob.getFileList().split(",");
			}
			if(ids.length==0){
				return Response.status(200).entity(SuccessReturn.build(new ArrayList())).build();
			}
			List<FileList> list = fileListMapper.selectByIds(ids);
			if(list==null){
				list = new ArrayList<FileList>();
			}
			return Response.status(200).entity(SuccessReturn.build(list)).build();
		} catch (BaseException e) {
			logger.error("查询job【/model/jobFiles】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询job【/model/jobFiles】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}
	
	
	@POST
	@Path("update-jobfiles")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response updateJobFiles(ModelJobWithBLOBs modelJob,
			@HeaderParam("Authorization") String token) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.isLogin(token);
			if(modelJob==null){
				throw new BaseException(1,"参数不能为空");
			}
			modelJobService.updateJobFile(modelJob.getTid(), modelJob.getFileList());
			return Response.status(200).entity(SuccessReturn.build(modelJob)).build();
		} catch (BaseException e) {
			logger.error("修改模型训练文件【/model/update-jobfiles】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("修改模型训练文件【/model/update-jobfiles】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "操作失败")).build();
		}
	}
	
	
	@GET
	@Path("explain/list/{jobId}")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "查看模型列表", notes = "查看模型列表")
	public Response getList(@HeaderParam("Authorization") String token,
			@PathParam("jobId") String jobId){
		try {
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	List<Map<String,Object>> list  = modelExplainService.list(jobId);
            return Response.status(200).entity(SuccessReturn.build(list)).build();
        } catch (BaseException e) {
            logger.error("查看模型列表【/model/explain/{}】,err:{}",jobId , e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("查看模型列表【/model/explain/{}】,err:{}",jobId,e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查看模型列表")).build();
        }
	}
	
	
	@GET
	@Path("predict-history")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "预测历史", notes = "预测历史")
	public Response getPredictHistory(@HeaderParam("Authorization") String token,
			@QueryParam("jobId") String jobId,
			@QueryParam("sequence") int sequence,
			@QueryParam("projectId") String projectId,
			@QueryParam("modelName") String modelName){
		try {
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	List<PredictResult>  list  = predictResultServcie.predictHistory(projectId,jobId,sequence,modelName);
            return Response.status(200).entity(SuccessReturn.build(list)).build();
        } catch (BaseException e) {
            logger.error("预测历史【/model/predict-history/{}】,err:{}",jobId , e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("预测历史【/model/predict-history/{}】,err:{}",jobId,e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "预测历史列表")).build();
        }
	}
	
	
	@POST
	@Path("create")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response create(@HeaderParam("Authorization") String token, ModelInfo modelInfo) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			String userId = loginService.getLoginUserId(token);
			modelInfo = modelService.create(userId, modelInfo);
			return Response.status(200).entity(SuccessReturn.build(modelInfo)).build();
		} catch (BaseException e) {
			logger.error("新建模型【/model/create】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("新建模型【/model/create】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "操作失败")).build();
		}
	}
	
	@GET
	@Path("delete-job-file")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response deleteJobFile(@HeaderParam("Authorization") String token, 
			@QueryParam("jobId") String jobId,
			@QueryParam("fileId") String fileId) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			String userId = loginService.getLoginUserId(token);
			modelJobService.deleteJobFile(userId,jobId, fileId);
			return Response.status(200).entity(SuccessReturn.build("success")).build();
		} catch (BaseException e) {
			logger.error("新建模型【/model/create】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("新建模型【/model/create】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "操作失败")).build();
		}
	}
	
}
