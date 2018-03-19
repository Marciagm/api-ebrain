package com.ebrain.api.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebrain.api.entities.DataCheckResult;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.service.DataCheckResultService;
import com.ebrain.api.util.FailureReturn;
import com.ebrain.api.util.SuccessReturn;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path("/datacheckresult")
@Api(value = "/datacheckresult")
public class DataCheckResultEndpoint {
	private Logger logger = LoggerFactory.getLogger(DataCheckResultEndpoint.class);
	@Autowired
	private DataCheckResultService dataCheckResultService;
	
	@GET
	@Path("detail/{tid}")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "查看模型解释", notes = "查看模型解释")
	public Response detail(@HeaderParam("Authorization") String token,
			@PathParam("tid") String tid){
		try {
        	if(StringUtils.isEmpty(token)){
        		throw new BaseException(1,"未登录");
        	}
        	DataCheckResult dataCheckResult  = dataCheckResultService.detail(tid);
            return Response.status(200).entity(SuccessReturn.build(dataCheckResult)).build();
        } catch (BaseException e) {
            logger.error("查看模型解释【/datacheckresult/detail/{}】,err:{}",tid , e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("查看模型解释【/datacheckresult/detail/{}】,err:{}",tid,e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
        }
	}
}
