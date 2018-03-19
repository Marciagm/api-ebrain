package com.ebrain.api.auth;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.ebrain.api.exceptions.BaseException;
import com.ebrain.api.service.LoginService;
import com.ebrain.api.util.SysConfig;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Context
	private HttpServletRequest request;
	@Autowired LoginService loginService;
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP Authorization header from the request
        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith(SysConfig.PRE_TOKEN)) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring(SysConfig.PRE_TOKEN.length()).trim();

        try {
            // Validate the token
            validateToken(token,request.getRemoteAddr());

        } catch (BaseException e) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build());
        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
            }
    }

    private void validateToken(String token,String clientId) throws BaseException {
    	boolean flag = loginService.isLogin(token);
    	
    	if(!flag){
    		throw new BaseException(1,"未登录");
    	}
    }
}
