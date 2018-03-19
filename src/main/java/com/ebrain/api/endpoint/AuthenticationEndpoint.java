package com.ebrain.api.endpoint;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

import com.ebrain.api.auth.Credentials;
import com.ebrain.api.entities.User;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.service.AlgorithmParam;
import com.ebrain.api.service.AlgorithmService;
import com.ebrain.api.service.LoginService;
import com.ebrain.api.util.FailureReturn;
import com.ebrain.api.util.SuccessReturn;

import io.swagger.annotations.Api;

@Component
@Path("/auth")
@Api(value="/auth")
public class AuthenticationEndpoint {
	private Logger logger = LoggerFactory.getLogger(AuthenticationEndpoint.class); 
	@Autowired private LoginService loginService;
	@Autowired private AlgorithmService algorithmService;
	@POST
	@Produces({ MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(Credentials form,@Context HttpServletRequest request) {
        try {
            if(form == null){
            	throw new BaseException(1,"参数为空");
            }
            User user = loginService.login(form);
            return Response.ok(SuccessReturn.build(user)).build();

        }  catch (BaseException e) {
            return Response.ok(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            return  Response.ok(FailureReturn.build(1, "服务错误")).build();
        }
    }
	
	@GET
	@Path("/logout")
	@Produces({ MediaType.APPLICATION_JSON})
    public Response logOut(@HeaderParam("Authorization") String accesstoken,
    		@QueryParam("projectId") String projectId,
			@QueryParam("jobId") String jobId,
			@QueryParam("sequence") Integer sequence) {
        try {        	 
        	loginService.logout(accesstoken);
        	if(StringUtils.isEmpty(jobId)){
        		AlgorithmParam algorithmParam = new AlgorithmParam();
        		algorithmParam.setJobId(jobId);
        		algorithmParam.setProjectId(projectId);
        		algorithmParam.setSequence(sequence);
        		algorithmService.stop(algorithmParam);
        	}
            return Response.ok(SuccessReturn.build("success")).build();
        }  catch (BaseException e) {
        	logger.error("【{}】下线异常,err:{}",accesstoken,e.getMessage());
            return Response.ok(e.build()).build();
        } catch (Exception e) {
        	logger.error("【{}】下线异常,err:{}",accesstoken,e.getMessage());
            return  Response.ok(FailureReturn.build(1, "服务错误")).build();
        }
    }

}
