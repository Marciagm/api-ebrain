package com.ebrain.api.util;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingResponseFilter
		implements ContainerResponseFilter {

	private static final Logger logger = LoggerFactory.getLogger(LoggingResponseFilter.class);

	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		String method = requestContext.getMethod();
		Object entity = responseContext.getEntity();
		if (entity != null) {
			logger.debug("【{}】【{}】Response:{} ",method, requestContext.getUriInfo().getPath(), new ObjectMapper().writeValueAsString(entity));
		}

	}


}
