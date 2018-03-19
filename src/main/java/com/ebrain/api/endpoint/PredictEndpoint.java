package com.ebrain.api.endpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebrain.api.entities.PredictDetail;
import com.ebrain.api.entities.PredictResult;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.service.LoginService;
import com.ebrain.api.service.PredictDetailServcie;
import com.ebrain.api.service.PredictResultServcie;
import com.ebrain.api.util.FailureReturn;
import com.ebrain.api.util.SuccessReturn;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path("/predict")
@Api(value = "/predict")
public class PredictEndpoint {
	private Logger logger = LoggerFactory.getLogger(PredictEndpoint.class);
	
	@Autowired
	private PredictResultServcie predictResultServcie;
	
	@Autowired
	private PredictDetailServcie predictDetailServcie;
	
	@Autowired
	private LoginService loginService;
	@GET
	@Path("result/{tid}")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "查看预测详情", notes = "查看预测详情")
	public Response result(@HeaderParam("Authorization") String token,
			@PathParam("tid") String tid){
		try {
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	PredictResult predictResult  = predictResultServcie.detail(tid);
        	PredictDetail predictDetail = predictDetailServcie.detail(tid);
        	Map<String,Object> map = new HashMap<String,Object>();
        	map.put("result", predictResult.getPredictResult());
        	map.put("detail", predictDetail.getDetail());
        	map.put("predictFile", predictResult.getPredictFile());
        	map.put("detailFile", predictDetail.getDetailFile());
            return Response.status(200).entity(SuccessReturn.build(map)).build();
        } catch (BaseException e) {
            logger.error("查看预测详情【/predict/result/{}】,err:{}",tid , e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("查看预测详情【/predict/result/{}】,err:{}",tid,e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
        }
	}
	
	@POST
	@Path("save")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "保存预测历史", notes = "保存预测历史")
	public Response savePredictHistory(@HeaderParam("Authorization") String token,
			PredictResult record){
		try {
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	String userId = loginService.getLoginUserId(token);
        	PredictResult predictResult  = predictResultServcie.create(userId, record);
            return Response.status(200).entity(SuccessReturn.build(predictResult)).build();
        } catch (BaseException e) {
            logger.error("保存预测历史【/predict/save】,err:{}" , e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("保存预测历史【/predict/save】,err:{}",e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "保存失败")).build();
        }
	}
	
	
	@GET
	@Path("download-file")
	@Produces(MediaType.APPLICATION_OCTET_STREAM) // 返回方式为流
	public byte[] downloadPridectFile(@QueryParam("fileName") String fileName,
			@HeaderParam("Authorization") String token,
			@Context HttpServletRequest request, @Context HttpServletResponse response) {
		FileInputStream fis = null;
		try {
			
			File file = new File(fileName);
			
			if(!file.exists()){
				throw new BaseException(1,"文件不存在");
			}
		
			fis = new FileInputStream(file);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			response.setHeader("Content-Disposition", "attachment;filename="+file.getName());// 为文件命名
			response.addHeader("content-type", "text/plain");
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
}
