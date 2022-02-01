package org.ashu.validation.config;
import java.io.IOException;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.springmvc.OpenApiValidationFilter;
import com.atlassian.oai.validator.springmvc.OpenApiValidationInterceptor;
import com.atlassian.oai.validator.springmvc.SpringMVCLevelResolverFactory;

@Configuration
public class OpenApiValidationConfig {
    @Bean
    public Filter validationFilter() {
        return new OpenApiValidationFilter(
                true, // enable request validation
                false  // enable response validation
        );
    }

    @Bean
    public WebMvcConfigurer addOpenApiValidationInterceptor(@Value("${open.api.spec.url}") final String specificationUrl) throws IOException {
        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor(specificationUrl)
                .withLevelResolver(SpringMVCLevelResolverFactory.create())
                .withBasePathOverride("/v1")
                .build();
        final OpenApiValidationInterceptor openApiValidationInterceptor = new OpenApiValidationInterceptor(validator);
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(final InterceptorRegistry registry) {
                registry.addInterceptor(openApiValidationInterceptor);
            }
        };
    }
}