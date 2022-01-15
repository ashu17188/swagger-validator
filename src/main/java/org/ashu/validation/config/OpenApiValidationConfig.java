package org.ashu.validation.config;

import java.io.IOException;
import java.util.List;

import org.ashu.validation.GenericRequestValidator;
import org.ashu.validation.OpenApiSpecsSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.springmvc.OpenApiValidationFilter;
import com.atlassian.oai.validator.springmvc.OpenApiValidationInterceptor;

@EnableCaching
@Configuration
public class OpenApiValidationConfig implements WebMvcConfigurer{
	
	private final MultiValueMap<String, OpenApiValidationInterceptor> validationInterceptor = new LinkedMultiValueMap<>();
	
	@Value("${schema.validation.validate-response:false}")
	private boolean validateResponse;
	
	@Autowired
	public OpenApiValidationConfig(final List<OpenApiSpecsSupplier> openApiSpecsSuppliers, final List<GenericRequestValidator> genericRequestValidators) throws IOException {
		for(OpenApiSpecsSupplier  openApiSpecsSupplier: openApiSpecsSuppliers) {
			LevelResolver.Builder builderLevelResolver	=LevelResolver.create();
			openApiSpecsSupplier.getLevelOverrides().forEach(builderLevelResolver::withLevel);
			OpenApiInteractionValidator.Builder builder =OpenApiInteractionValidator.createForSpecificationUrl(openApiSpecsSupplier.getOpenApiSecsUrl())
			.withLevelResolver(builderLevelResolver.build());
			if(openApiSpecsSupplier.getBasePathOverride().isPresent()) {
				builder.withBasePathOverride(openApiSpecsSupplier.getBasePathOverride().get());
			}
			genericRequestValidators.forEach(builder::withCustomRequestValidation);
			if(openApiSpecsSupplier.getCustomRequestValidators().isPresent()) {
				openApiSpecsSupplier.getCustomRequestValidators().get().forEach(builder:: withCustomRequestValidation);
			}
			if(openApiSpecsSupplier.getWhitelist().isPresent()) {
				builder.withWhitelist(openApiSpecsSupplier.getWhitelist().get());
			}
			validationInterceptor.add(openApiSpecsSupplier.getPathPattern(), new OpenApiValidationInterceptor(builder.build()));
			
		}
		
	}
	
	@Override
	 public void addInterceptors(InterceptorRegistry registry) {
		validationInterceptor.forEach((pathPattern, validationInterceptors)-> 
		validationInterceptors.forEach(validationInterceptor -> 
		registry.addInterceptor(validationInterceptor).addPathPatterns(pathPattern)));
	}
	
	@Bean
	public FilterRegistrationBean<OpenApiValidationFilter> openApiFilterBean(){
		FilterRegistrationBean<OpenApiValidationFilter> registrationBean   =  new FilterRegistrationBean<>();
		OpenApiValidationFilter openApiValidationFilter = new OpenApiValidationFilter(true, validateResponse);
		registrationBean.setFilter(openApiValidationFilter);
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10 );
		return registrationBean;
	}
}
//	@Bean
//	public Filter validationFilter() {
//		return new OpenApiValidationFilter(true, // enable request validation
//				true // enable response validation
//		);
//	}
//
////    @Bean
////    public WebMvcConfigurer addOpenApiValidationInterceptor(@Value("classpath:api-spring-test.json") final Resource apiSpecification) throws IOException {
////        final EncodedResource specResource = new EncodedResource(openApiSpecification, StandardCharsets.UTF_8);
////        final OpenApiValidationInterceptor openApiValidationInterceptor = new OpenApiValidationInterceptor(encodedResource);
////        return new WebMvcConfigurer() {
////            @Override
////            public void addInterceptors(final InterceptorRegistry registry) {
////                registry.addInterceptor(openApiValidationInterceptor);
////            }
////        };
////    }
//
//	@Bean
//	public WebMvcConfigurer addOpenApiValidationInterceptor(@Value("${open.api.spec.url:myspec.yaml}") final String specificationUrl)
//			throws IOException {
//		final OpenApiInteractionValidator validator = OpenApiInteractionValidator
//				.createForSpecificationUrl(specificationUrl).withCustomRequestValidation(new JsonPathMandatoryValidator())
////				.withCustomResponseValidation(new SimpleResponseValidator())
//				.withLevelResolver(SpringMVCLevelResolverFactory.create()).withBasePathOverride("/v1").build();
//		final OpenApiValidationInterceptor openApiValidationInterceptor = new OpenApiValidationInterceptor(validator);
//		return new WebMvcConfigurer() {
//			@Override
//			public void addInterceptors(final InterceptorRegistry registry) {
//				registry.addInterceptor(new RequestLoggingInterceptor());
//				registry.addInterceptor(openApiValidationInterceptor);
//			}
//		};
//	}
//
////	private class SimpleRequestValidator implements CustomRequestValidator {
////		@Override
////		public ValidationReport validate(@Nonnull Request request, @Nonnull ApiOperation apiOperation) {
////			if (apiOperation.getOperation().getExtensions().containsKey("x-some-extension")) {
////				if (!request.getHeaderValue("foo").isPresent()) {
////					return ValidationReport
////							.singleton((ValidationReport.Message.create("some.extension", "Required header foo missing.").build()));
////				}
////			}
////			return ValidationReport.empty();
////		}
////	}
////
////	private class SimpleResponseValidator implements CustomResponseValidator {
////		@Override
////		public ValidationReport validate(@Nonnull Response response, @Nonnull ApiOperation apiOperation) {
////			if (apiOperation.getOperation().getExtensions().containsKey("x-some-extension")) {
////				if (!response.getHeaderValue("foo").isPresent()) {
////					return ValidationReport
////							.singleton((ValidationReport.Message.create("some.extension", "Required header foo missing.").build()));
////				}
////			}
////			return ValidationReport.empty();
////		}
////	}
//
//	static class RequestLoggingInterceptor implements HandlerInterceptor {
//
//		private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
//
//		@Override
//		public boolean preHandle(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse,
//				final Object handler) throws Exception {
//			final String requestLog = String.join(System.lineSeparator(),
//					"uri=" + servletRequest.getRequestURI() + "?" + servletRequest.getQueryString(),
//					"client=" + servletRequest.getRemoteAddr(), "payload=" + getPayload(servletRequest));
//			LOG.info("Incoming request: {}", requestLog);
//			return true;
//		}
//
//		private String getPayload(final HttpServletRequest request) throws IOException {
//			if (request instanceof ResettableRequestServletWrapper) {
//				final ResettableRequestServletWrapper resettableRequest = (ResettableRequestServletWrapper) request;
//				final char[] chunk = new char[1024]; // only log the first 1kB
//				IOUtils.read(request.getReader(), chunk);
//				// reset the input stream - so it can be read again by the next interceptor /
//				// filter
//				resettableRequest.resetInputStream();
//				return new String(chunk);
//			} else {
//				return "[unknown]";
//			}
//		}
//	}
