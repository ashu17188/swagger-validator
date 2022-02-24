package org.ashu.validation.config;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class AppConfig {

	@Primary
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.registerModule(new JavaTimeModule());
		mapper.findAndRegisterModules();
		mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {

			@Override
			public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt)
					throws IOException, JsonProcessingException {
				DateTimeFormatter formatter = new java.time.format.DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ISO_DATE)
						.appendOptional(DateTimeFormatter.ofPattern("'T'"))
						.appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss"))
						.appendOptional(DateTimeFormatter.ofPattern(".SSS"))
						.appendOptional(DateTimeFormatter.ofPattern("X"))
						.toFormatter();
				try {
					return OffsetDateTime.parse(jsonParser.getText(), formatter);
				} catch (DateTimeException e) {
					LocalDateTime dateTime = LocalDateTime.of(LocalDate.parse(jsonParser.getText(), formatter), LocalTime.MIN);
					ZoneId zone = ZoneId.systemDefault();
					ZonedDateTime zoned = dateTime.atZone(zone);
					return zoned.toOffsetDateTime();
				}
			}
		});
		mapper.registerModule(simpleModule);
		return mapper;
	}
}
