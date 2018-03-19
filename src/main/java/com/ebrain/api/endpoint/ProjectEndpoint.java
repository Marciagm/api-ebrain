package com.ebrain.api.endpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebrain.api.entities.DataCheckResultWithBLOBs;
import com.ebrain.api.entities.FileList;
import com.ebrain.api.entities.ModelExplain;
import com.ebrain.api.entities.ModelJob;
import com.ebrain.api.entities.PredictDetail;
import com.ebrain.api.entities.PredictResult;
import com.ebrain.api.entities.Project;
import com.ebrain.api.entities.TrainedResult;
import com.ebrain.api.entities.User;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.service.AlgorithmParam;
import com.ebrain.api.service.AlgorithmService;
import com.ebrain.api.service.ChartService;
import com.ebrain.api.service.DataCheckResultService;
import com.ebrain.api.service.FileListService;
import com.ebrain.api.service.LoginService;
import com.ebrain.api.service.ModelExplainService;
import com.ebrain.api.service.ModelJobService;
import com.ebrain.api.service.PredictDetailServcie;
import com.ebrain.api.service.PredictOnlineService;
import com.ebrain.api.service.PredictResultServcie;
import com.ebrain.api.service.ProjectService;
import com.ebrain.api.service.TrainedResultService;
import com.ebrain.api.util.FailureReturn;
import com.ebrain.api.util.JsonUtil;
import com.ebrain.api.util.PDFTemplate;
import com.ebrain.api.util.SuccessReturn;
import com.ebrain.api.util.SysConfig;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.swagger.annotations.Api;

@Component
@Path("/project")
@Api(value = "/project")
public class ProjectEndpoint {
	private Logger logger = LoggerFactory.getLogger(ProjectEndpoint.class);

	@Autowired
	private ModelJobService modelJobService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ModelExplainService modelExplainService;
	@Autowired
	private DataCheckResultService dataCheckResultService;
	@Autowired
	private PredictResultServcie predictResultServcie;
	@Autowired
	private TrainedResultService trainedResultService;
	@Autowired
	private FileListService fileListService;
	@Autowired
	private PredictDetailServcie predictDetailServcie;

	@Autowired
	private AlgorithmService algorithmService;
	@Autowired
	private PredictOnlineService predictOnlineService;
	@Autowired
	private  ChartService chartService;
	/**
	 * 创建项目
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("create")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response create(@HeaderParam("Authorization") String token, Project project) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			String userId = loginService.getLoginUserId(token);
			project = projectService.create(userId, project);
			return Response.status(200).entity(SuccessReturn.build(project)).build();
		} catch (BaseException e) {
			logger.error("新建项目【/project/create】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("新建项目【/project/create】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "操作失败")).build();
		}
	}

	/**
	 * 项目列表
	 * 
	 * @param token
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GET
	@Path("list")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response list(@HeaderParam("Authorization") String token,
			@DefaultValue("1") @QueryParam("pageNum") Integer pageNum,
			@DefaultValue("10") @QueryParam("pageSize") Integer pageSize) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			String userId = loginService.getLoginUserId(token);
			PageInfo<Project> page = projectService.list(userId, pageNum, pageSize);
			return Response.status(200).entity(SuccessReturn.build(page)).build();
		} catch (BaseException e) {
			logger.error("查看项目列表【/project/list】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查看项目列表【/project/list】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}

	/**
	 * 项目详情
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("update")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response update(@HeaderParam("Authorization") String token, Project project) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			String userId = loginService.getLoginUserId(token);
			project = projectService.update(userId, project);
			return Response.status(200).entity(SuccessReturn.build(project)).build();
		} catch (BaseException e) {
			logger.error("修改项目【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("修改项目【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "操作失败")).build();
		}
	}

	/**
	 * 项目详情
	 * 
	 * @param token
	 * @param projectId
	 * @return
	 */
	@GET
	@Path("detail/{projectId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response detail(@HeaderParam("Authorization") String token, @PathParam("projectId") String projectId) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}

			Project project = projectService.detail(projectId);
			return Response.status(200).entity(SuccessReturn.build(project)).build();
		} catch (BaseException e) {
			logger.error("项目详情【/project/detail/{}】,err:{}", projectId, e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("项目详情【/project/detail/{}】,err:{}", projectId, e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}

	/**
	 * 新建job
	 * 
	 * @param projectId
	 * @param token
	 * @param modelJob
	 * @return
	 */
	@GET
	@Path("new-job/{projectId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response newJob(@PathParam("projectId") String projectId,@QueryParam("projectId") String fileList,
			@HeaderParam("Authorization") String token) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			
			String userId = loginService.getLoginUserId(token);

			if (fileList==null) {
				throw new BaseException(1, "缺少参数fileList");
			}
			ModelJob modelJob = modelJobService.create(userId, projectId, fileList);
			return Response.status(200).entity(SuccessReturn.build(modelJob)).build();
		} catch (BaseException e) {
			logger.error("新建job【/project/new-job】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("新建job【/project/new-job】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "操作失败")).build();
		}
	}

	@GET
	@Path("get-source-file-data")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getSourceFileData(@QueryParam("fileId") String fileId, @HeaderParam("Authorization") String token) {
		try {
			if (StringUtil.isEmpty(fileId)) {
				throw new BaseException(1, "jobId不能为空");
			}

			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.getLoginUserId(token);
			FileList fileList = fileListService.detail(fileId);
			return Response.status(200).entity(SuccessReturn.build(fileList)).build();
		} catch (BaseException e) {
			logger.error("查询数据预处理结果【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询数据预处理结果【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}

	/**
	 * 查询数据预处理结果
	 * 
	 * @param jobId
	 * @param sequence
	 * @param token
	 * @return
	 */
	@GET
	@Path("get-data-result")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getDataResult(@QueryParam("projectId") String projectId, @QueryParam("jobId") String jobId,
			@QueryParam("sequence") int sequence, @HeaderParam("Authorization") String token) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.getLoginUserId(token);
			DataCheckResultWithBLOBs dataCheckResult = dataCheckResultService.detail(projectId, jobId, sequence);
			if(dataCheckResult==null){
				int retry=3;
				while(true){
					if(retry==0){
						throw new BaseException(1,"结果还未生成");
					}
					retry--;
					Thread.sleep(2000l);
					dataCheckResult= dataCheckResultService.detail(projectId, jobId, sequence);
				}
			}
			return Response.status(200).entity(SuccessReturn.build(dataCheckResult)).build();
		} catch (BaseException e) {
			logger.error("查询数据预处理结果【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询数据预处理结果【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}

	/**
	 * 查询训练结果
	 * 
	 * @param jobId
	 * @param sequence
	 * @param token
	 * @return
	 */
	@GET
	@Path("get-train-result")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getTrainedResult(@QueryParam("projectId") String projectId, @QueryParam("jobId") String jobId,
			@QueryParam("sequence") Integer sequence, @HeaderParam("Authorization") String token) {
		try {
			if (StringUtil.isEmpty(projectId)) {
				throw new BaseException(1, "projectId不能为空");
			}
			if (StringUtil.isEmpty(jobId)) {
				throw new BaseException(1, "jobId不能为空");
			}
			if (sequence == null) {
				throw new BaseException(1, "sequence不能为空");
			}
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.getLoginUserId(token);
			List<TrainedResult> list = trainedResultService.detail(projectId, jobId, sequence);
			return Response.status(200).entity(SuccessReturn.build(list)).build();
		} catch (BaseException e) {
			logger.error("查询训练结果【/project/get-train-result】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询训练结果【/project/get-train-result】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}

	/**
	 * 查询模型解释
	 * 
	 * @param jobId
	 * @param sequence
	 * @param token
	 * @return
	 */
	@GET
	@Path("get-model-explain")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getModelExplain(@QueryParam("projectId") String projectId, @QueryParam("jobId") String jobId,
			@QueryParam("sequence") Integer sequence, @HeaderParam("Authorization") String token) {
		try {
			if (StringUtil.isEmpty(projectId)) {
				throw new BaseException(1, "projectId不能为空");
			}
			if (StringUtil.isEmpty(jobId)) {
				throw new BaseException(1, "jobId不能为空");
			}
			if (sequence == null) {
				throw new BaseException(1, "sequence不能为空");
			}
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.getLoginUserId(token);
			ModelExplain modelExplain = modelExplainService.detail(projectId, jobId, sequence);
			return Response.status(200).entity(SuccessReturn.build(modelExplain)).build();
		} catch (BaseException e) {
			logger.error("查询模型解释【/project/get-train-result】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询模型解释【/project/get-train-result】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}

	/**
	 * 查询预测结果
	 * 
	 * @param jobId
	 * @param sequence
	 * @param token
	 * @return
	 */
	@GET
	@Path("get-predict-result")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getPredictResult(@QueryParam("projectId") String projectId, 
			@QueryParam("jobId") String jobId,
			@QueryParam("sequence") Integer sequence, 
			@QueryParam("modelName") String modelName, 
			@HeaderParam("Authorization") String token) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			
			if (StringUtil.isEmpty(projectId)) {
				throw new BaseException(1, "projectId不能为空");
			}
			if (StringUtil.isEmpty(jobId)) {
				throw new BaseException(1, "jobId不能为空");
			}
			if (sequence == null) {
				throw new BaseException(1, "sequence不能为空");
			}
			if (StringUtil.isEmpty(modelName)) {
				throw new BaseException(1, "modelName不能为空");
			}
			
			loginService.getLoginUserId(token);
			PredictResult predictResult = predictResultServcie.detail(projectId, jobId, sequence,modelName);
			return Response.status(200).entity(SuccessReturn.build(predictResult)).build();
		} catch (BaseException e) {
			logger.error("查询预测结果【/project/get-predict-result】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询预测结果【/project/get-predict-result】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}

	/**
	 * 查询预测结果
	 * 
	 * @param jobId
	 * @param sequence
	 * @param token
	 * @return
	 */
	@GET
	@Path("get-predict-detail")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getPredictDetail(@QueryParam("projectId") String projectId, @QueryParam("jobId") String jobId,
			@QueryParam("sequence") Integer sequence, @HeaderParam("Authorization") String token) {
		try {
			if (StringUtil.isEmpty(projectId)) {
				throw new BaseException(1, "projectId不能为空");
			}
			if (StringUtil.isEmpty(jobId)) {
				throw new BaseException(1, "jobId不能为空");
			}
			if (sequence == null) {
				throw new BaseException(1, "sequence不能为空");
			}
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.getLoginUserId(token);
			PredictDetail predictDetail = predictDetailServcie.detail(projectId, jobId, sequence);
			return Response.status(200).entity(SuccessReturn.build(predictDetail)).build();
		} catch (BaseException e) {
			logger.error("查询详细报告【/project/get-predict-detail】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询详细报告【/project/get-predict-detail】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}

	@GET
	@Path("download-predict-result/{projectId}/{jobId}/{sequence}/{modelName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM) // 返回方式为流
	public byte[] downloadPredictResult(@PathParam("projectId") String projectId, 
			@PathParam("jobId") String jobId,
			@PathParam("sequence") Integer sequence, 
			@PathParam("modelName") String modelName, 
			@HeaderParam("Authorization") String token,
			@Context HttpServletRequest request, @Context HttpServletResponse response) {
		FileInputStream fis = null;
		try {

			// 第一步，创建一个webbook，对应一个Excel文件
			HSSFWorkbook wb = new HSSFWorkbook();
			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
			HSSFSheet sheet = wb.createSheet("预测结果");
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			HSSFRow row = sheet.createRow((int) 0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);


			// 第五步，写入实体数据 实际应用中这些数据从数据库得到，
			
			PredictResult predictResult = predictResultServcie.detail(projectId, jobId, sequence,modelName);
			String result = predictResult.getPredictResult();
			JsonObject json = JsonUtil.toJsonObject(result);
			JsonArray array = json.get("dataList").getAsJsonArray();

			JsonArray jsons = array.get(0).getAsJsonArray();
			HSSFCell cell;
			int celIndex=0;
			/*Iterator iterator = jsons.keySet().iterator();
			String key, value;
			HSSFCell cell;
			int celIndex=0;
			while (iterator.hasNext()) {
				cell = row.createCell((short) celIndex);
				key = (String) iterator.next();
				value = json.get(key).getAsString();
				cell.setCellValue(value);
				celIndex++;
			}*/
			for(int i=0;i<jsons.size();i++){
				cell = row.createCell((short) celIndex);
				cell.setCellValue(jsons.get(i).getAsString());
				celIndex++;
			}
			
			for (int i = 1; i < array.size(); i++) {
				row = sheet.createRow(i + 1);
			
				jsons = array.get(i).getAsJsonArray();
				celIndex=0;
				/*iterator = json.keySet().iterator();
				celIndex=0;
				while (iterator.hasNext()) {
					key = (String) iterator.next();
					value = json.get(key).getAsString();
					row.createCell(celIndex).setCellValue(json.get(""+celIndex).getAsString());
					celIndex++;
				}*/
				for(int j=0;j<jsons.size();j++){
					row.createCell(celIndex).setCellValue(jsons.get(j).getAsString());
					celIndex++;
				}
			}
			// 第六步，将文件存到指定位置
			File tempFile = new File(projectId + "_" + jobId + "_" + sequence + ".xls");
			try {
				FileOutputStream fout = new FileOutputStream(tempFile);
				wb.write(fout);
				wb.close();
				fout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			fis = new FileInputStream(tempFile);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			response.setHeader("Content-Disposition", "attachment;filename=predict_result.xls");// 为文件命名
			response.addHeader("content-type", "application/vnd.ms-excel;charset=utf-8");
			fis.close();
			tempFile.delete();
			return b;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (BaseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("download-report/{projectId}/{jobId}/{sequence}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM) // 返回方式为流
	public byte[] downloadReport(@PathParam("projectId") String projectId, @PathParam("jobId") String jobId,
			@PathParam("sequence") Integer sequence, @HeaderParam("Authorization") String token,
			@Context HttpServletRequest request, @Context HttpServletResponse response) {
		FileInputStream fis = null;
		try {
			if (StringUtil.isEmpty(projectId)) {
				throw new BaseException(1, "projectId不能为空");
			}
			if (StringUtil.isEmpty(jobId)) {
				throw new BaseException(1, "jobId不能为空");
			}
			if (sequence == null) {
				throw new BaseException(1, "sequence不能为空");
			}
			
			String output=SysConfig.getProperty("ebrain[temp_output]", "D:\\test\\pdf\\")+"\\"+projectId+"\\"+jobId+"\\"+sequence;
			File file = new File(output);
			if(!file.exists()){
				if(!file.mkdirs()){
					throw new BaseException(1,"输出目录不存在");
				}
			}
			file = new File(output+"\\report.pdf");
			if(!file.exists()){
				//FileList fileList = fileListService.detail(fileId);
				DataCheckResultWithBLOBs dataCheckResult = dataCheckResultService.detail(projectId, jobId, sequence);
				if(dataCheckResult==null){
					throw new BaseException(1,"数据不存在");
				}
				ModelExplain modelExplain = modelExplainService.detail(projectId, jobId, sequence);
				if(modelExplain==null){
					throw new BaseException(1,"模型解释未生成");
				}
				Map<String,String> map = chartService.genPdf(dataCheckResult,modelExplain,output);
				PDFTemplate pdfTemplate =new PDFTemplate();
				file = pdfTemplate.gen(dataCheckResult,modelExplain,output,map);
			}
		
			fis = new FileInputStream(file);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			response.setHeader("Content-Disposition", "attachment;filename=report.pdf");// 为文件命名
			response.addHeader("content-type", "application/pdf");
			file=null;	
			return b;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "数据错误".getBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return "数据错误".getBytes();
		} catch (BaseException e) {
			e.printStackTrace();
			return e.getMessage().getBytes();
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				return "数据错误".getBytes();
			}
		}
	}

	@GET
	@Path("get-jobinfo")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getJobInfo(@QueryParam("projectId") String projectId, @QueryParam("jobId") String jobId,
			@QueryParam("sequence") Integer sequence, @HeaderParam("Authorization") String token) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			if (!loginService.isLogin(token)) {
				throw new BaseException(1, "未登录");
			}
			if (StringUtil.isEmpty(projectId)) {
				throw new BaseException(1, "projectId不能为空");
			}
			if (StringUtil.isEmpty(jobId)) {
				throw new BaseException(1, "jobId不能为空");
			}
			if (sequence == null) {
				throw new BaseException(1, "sequence不能为空");
			}
			ModelJob modelJob = modelJobService.detail(jobId, sequence);
			return Response.status(200).entity(SuccessReturn.build(modelJob)).build();
		} catch (BaseException e) {
			logger.error("查询job【/project/get-jobinfo】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询job【/project/get-jobinfo】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}
	
	@GET
	@Path("get-job-list")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getJobList(@QueryParam("projectId") String projectId,
			@HeaderParam("Authorization") String token) {
		try {
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			if (!loginService.isLogin(token)) {
				throw new BaseException(1, "未登录");
			}
			if (StringUtil.isEmpty(projectId)) {
				throw new BaseException(1, "projectId不能为空");
			}
			
			List<ModelJob> list = modelJobService.getJobList(projectId);
			return Response.status(200).entity(SuccessReturn.build(list)).build();
		} catch (BaseException e) {
			logger.error("查询job【/project/get-jobinfo】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询job【/project/get-jobinfo】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}
	

	/**
	 * 执行预测
	 * 
	 * @param algorithmParam
	 * @param token
	 * @return
	 */
	@POST
	@Path("predict-offline")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response runPredict(AlgorithmParam algorithmParam, @HeaderParam("Authorization") String token) {
		try {
			if (StringUtil.isEmpty(algorithmParam.getProjectId())) {
				throw new BaseException(1, "projectId不能为空");
			}
			if (StringUtil.isEmpty(algorithmParam.getJobId())) {
				throw new BaseException(1, "jobId不能为空");
			}

			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.getLoginUserId(token);
			algorithmService.runPredict(algorithmParam);
			return Response.status(200).entity(SuccessReturn.build("running")).build();
		} catch (BaseException e) {
			logger.error("执行预测【/project/run-predict】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("执行预测【/project/run-predict】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "执行失败")).build();
		}
	}

	@POST
	@Path("predict-online")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response redictOnline(AlgorithmParam algorithmParam, @HeaderParam("Authorization") String token) {
		try {
			if (StringUtil.isEmpty(algorithmParam.getProjectId())) {
				throw new BaseException(1, "projectId不能为空");
			}
			if (StringUtil.isEmpty(algorithmParam.getJobId())) {
				throw new BaseException(1, "jobId不能为空");
			}

			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.getLoginUserId(token);
			JsonObject result = predictOnlineService.runPredict(algorithmParam);
			return Response.status(200).entity(SuccessReturn.build(result)).build();
		} catch (BaseException e) {
			logger.error("执行预测【/project/run-predict】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("执行预测【/project/run-predict】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "执行失败")).build();
		}
	}

	
	@POST
	@Path("run-step")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response runStep(AlgorithmParam algorithmParam, @HeaderParam("Authorization") String token) {
		try {
			
			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.getLoginUserId(token);
			if (StringUtil.isEmpty(algorithmParam.getProjectId())) {
				throw new BaseException(1, "projectId不能为空");
			}
			algorithmService.runStep(algorithmParam);
			return Response.status(200).entity(SuccessReturn.build("ok")).build();
		} catch (BaseException e) {
			logger.error("执行预测【/project/run-predict】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("执行预测【/project/run-predict】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "执行失败")).build();
		}
	}
	
	

	/**
	 * 运营版查看原始数据
	 * @param jobId
	 * @param token
	 * @return
	 */
	@GET
	@Path("get-file-data")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getFileData(@QueryParam("jobId") String jobId, @HeaderParam("Authorization") String token) {
		try {
			if (StringUtil.isEmpty(jobId)) {
				throw new BaseException(1, "jobId不能为空");
			}

			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			loginService.getLoginUserId(token);
			
			FileList fileList = fileListService.getFileData(jobId);
			return Response.status(200).entity(SuccessReturn.build(fileList)).build();
		} catch (BaseException e) {
			logger.error("查询数据预处理结果【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询数据预处理结果【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}
	
	@GET
	@Path("delete")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response delete(@QueryParam("projectId") String projectId, @HeaderParam("Authorization") String token) {
		try {
			

			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			if (StringUtil.isEmpty(projectId)) {
				throw new BaseException(1, "projectId不能为空");
			}
			String userId = loginService.getLoginUserId(token);
			
			projectService.delete(userId,projectId);
			return Response.status(200).entity(SuccessReturn.build("删除成功")).build();
		} catch (BaseException e) {
			logger.error("查询数据预处理结果【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询数据预处理结果【/project/update】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	}
	
	/**
	 * 查询job当前进度
	 * @param jobId
	 * @param token
	 * @return
	 */
	@GET
	@Path("get-job-progress")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getJobProgress(@QueryParam("jobId") String jobId, @HeaderParam("Authorization") String token) {
		try {
			

			if (StringUtils.isEmpty(token)) {
				throw new BaseException(1, "未登录");
			}
			if (StringUtil.isEmpty(jobId)) {
				throw new BaseException(1, "jobId不能为空");
			}
			loginService.isLogin(token);
			
			ModelJob progress = modelJobService.getJobStatus(jobId);
			return Response.status(200).entity(SuccessReturn.build(progress)).build();
		} catch (BaseException e) {
			logger.error("查询job当前进度【/project/get-job-progress】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
		} catch (Exception e) {
			logger.error("查询job当前进度【/project/get-job-progress】,err:{}", e.getMessage());
			return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
		}
	} 
}
