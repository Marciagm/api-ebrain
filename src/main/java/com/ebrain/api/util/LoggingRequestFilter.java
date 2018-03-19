package com.ebrain.api.util;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingRequestFilter
		implements ContainerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(LoggingRequestFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String method = requestContext.getMethod();
		logger.debug("【{}】【{}】 " ,method,requestContext.getUriInfo().getPath());
	}


}
