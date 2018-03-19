package com.ebrain.api.config;

import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;

import com.ebrain.api.endpoint.AuthenticationEndpoint;
import com.ebrain.api.util.CORSResponseFilter;
import com.ebrain.api.util.LoggingRequestFilter;
import com.ebrain.api.util.LoggingResponseFilter;

/**
 * Registers the components to be used by the JAX-RS application  
 * 
 * @author cys
 *
 */
public class Application extends ResourceConfig {

    /**
	* Register JAX-RS application components.
	*/	
	public Application(){
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		register(RequestContextFilter.class);
		register(LoggingRequestFilter.class);
		register(MultiPartFeature.class);	
		register(LoggingResponseFilter.class);
		register(CORSResponseFilter.class);
		register(AuthenticationEndpoint.class);
		packages("com.ebrain.api.endpoint");
		register(ValidationConfigurationContextResolver.class);
		register(io.swagger.jaxrs.listing.ApiListingResource.class); 
		register(io.swagger.jaxrs.listing.AcceptHeaderApiListingResource.class); 
		register(io.swagger.jaxrs.listing.SwaggerSerializers.class); 
	}
	public static class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig>{
        @Context
        private ResourceContext resourceContext;
        @Override
        public ValidationConfig getContext(final Class<?> type) {
            return new ValidationConfig().constraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class));
        }
    }
}
