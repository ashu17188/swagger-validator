package org.ashu.validation.test;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableWebMvc
@ComponentScan({"org.ashu.validation","org.ashu.validation.validator"})
public class TestConfig {

		@Bean
		public CacheManager cacheManager() {
			return new ConcurrentMapCacheManager("patterns");
		}
	
		
//		@Bean
//		@Primary
//		public ObjectMapper getTestObjectMapper() {
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//			mapper.registerModule(new Jdk8Module())
//			.registerModule(new JavaTimeModule())
//			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//			
//			return mapper;
//		}
}
