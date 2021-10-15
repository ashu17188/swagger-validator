package org.ashu.schema.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.atlassian.oai.validator.interaction.request.CustomRequestValidator;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;

public interface OpenApiSpecsSupplier {

	String VALIDATION_REQUEST_PATH_MISSING = "validation.request.path.missing";

	String getOpenApiSecsUrl();

	default String getPathPattern() {
		return "/**";
	}

	default Optional<String> getBasePathOverride() {
		return Optional.empty();
	}

	default Map<String, ValidationReport.Level> getLevelOverrides() {
		Map<String, ValidationReport.Level> levelOverrides = new HashMap<>();
		levelOverrides.put(VALIDATION_REQUEST_PATH_MISSING, ValidationReport.Level.INFO);
		return levelOverrides;
	}
	
	default Optional<ValidationErrorsWhitelist> getWhitelist(){
		return Optional.empty();
	}
	
	default Optional<List<CustomRequestValidator>> getCustomRequestValidators(){
		return Optional.empty();
	}
}
