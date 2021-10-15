package org.ashu.schema.validation.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.atlassian.oai.validator.report.ValidationReport;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class ValidationUtils {

	public <T> T readJsonResource(String jsonResourcePath, Class<T> tClass) {
		try (InputStream is = ValidationUtils.class.getResourceAsStream(jsonResourcePath)) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule())
					.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
			return mapper.readValue(is, tClass);
		} catch (IOException | NullPointerException e) {
			throw new IllegalArgumentException(String.format("Cannot convert the file in path %s to class %s due to %s",
					jsonResourcePath, tClass, e.getMessage()));
		}
	}

	@Cacheable("patterns")
	public Pattern getPatter(String patterString) {
		return Pattern.compile(patterString);
	}

	public ValidationReport mergeValidationReports(List<ValidationReport> validationReports) {
		return validationReports.stream().reduce(ValidationReport.empty(), ValidationReport::merge);

	}

	public ValidationReport processMessage(String messageKey, String messageFormat, List<String> errorJsonPaths) {
		return mergeValidationReports(errorJsonPaths.stream()
				.map(jsonPath -> ValidationReport
						.singleton((ValidationReport.Message.create(messageKey, String.format(messageFormat, jsonPath)).build())))
				.collect(Collectors.toList()));

	}

}