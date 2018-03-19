package com.ebrain.api.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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

import com.ebrain.api.entities.User;
import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.service.LoginService;
import com.ebrain.api.util.FailureReturn;
import com.ebrain.api.util.SuccessReturn;

import io.swagger.annotations.Api;

@Component
@Path("/user")
@Api(value = "/user")
public class UserEndpoint {
	private Logger logger = LoggerFactory.getLogger(UserEndpoint.class);
	@Autowired LoginService loginService;
	
	/**
	 * 用户信息查询
	 * @param id
	 * @param accesstoken
	 * @return
	 */
	@GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response detail(@PathParam("id") String id, @HeaderParam("Authorization") String accesstoken) {
        try {
        	if(StringUtils.isEmpty(id)){
        		throw new BaseException(1,"缺少参数");
        	}
            return Response.status(200).entity(SuccessReturn.build("")).build();
        } catch (BaseException e) {
            logger.error("用户详情【/user/{}】,err:{}", id, e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("用户详情【/user/{}】,err:{}", id, e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "查询失败")).build();
        }
    }
	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	@POST
	@Path("signup")
	@Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response signup(User user) {
        try {
        	if(user==null){
        		throw new BaseException(1,"缺少参数");
        	}
        	if(StringUtils.isEmpty(user.getNickname())){
        		throw new BaseException(1,"昵称不能为空");
        	}
        	if(StringUtils.isEmpty(user.getEmail())){
        		throw new BaseException(1,"邮箱不能为空");
        	}
        	user.setUsername(user.getEmail());
        	/*if(StringUtils.isEmpty(user.getCompany())){
        		throw new BaseException(1,"公司名称不能为空");
        	}*/
        	
        	if(StringUtils.isEmpty(user.getPhone())){
        		throw new BaseException(1,"联系电话不能为空");
        	}
        	if(StringUtils.isEmpty(user.getPassword())){
        		throw new BaseException(1,"密码不能为空");
        	}
        	user = loginService.signup(user);
            return Response.status(200).entity(SuccessReturn.build(user)).build();
        } catch (BaseException e) {
            logger.error("用户注册【/user/signup】,err:{}", e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, e.getMessage())).build();
        } catch (Exception e) {
            logger.error("用户注册【/user/signup】,err:{}",e.getMessage());
            return Response.status(200).entity(FailureReturn.build(1, "注册失败")).build();
        }
    }
	
}
