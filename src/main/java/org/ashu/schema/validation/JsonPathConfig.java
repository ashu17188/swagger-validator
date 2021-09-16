package org.ashu.schema.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;

@Configuration
public class JsonPathConfig {

	@Bean
	public com.jayway.jsonpath.Configuration getJsonPathConfigurations(){
		return com.jayway.jsonpath.Configuration.builder()
				.jsonProvider(new JacksonJsonNodeJsonProvider())
				.options(Option.SUPPRESS_EXCEPTIONS, Option.DEFAULT_PATH_LEAF_TO_NULL)
				.build();
	}
}
