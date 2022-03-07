package org.ashu.validation.jsonpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.ashu.validation.util.JsonProcessor;
import org.ashu.validation.util.ValidationUtils;
import org.ashu.validation.validator.JsonPathMandatoryValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.NormalisedPath;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.ValidationReport;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import io.swagger.v3.oas.models.PathItem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConditionalJsonPathMandatoryValidator extends JsonPathMandatoryValidator {

	private final JsonProcessor jsonProcessor;

	private List<ConditionalJsonPathSupplier> conditionalJsonPathSupplierList;

	public ConditionalJsonPathMandatoryValidator(List<ConditionalJsonPathSupplier> conditionalJsonPathSupplierList,
			Configuration conf, JsonProcessor jsonProcessor, ValidationUtils validationUtils) {
		super(Collections.EMPTY_LIST, conf, validationUtils);
		this.conditionalJsonPathSupplierList = conditionalJsonPathSupplierList;
		this.jsonProcessor = jsonProcessor;
	}

	@Override
	public ValidationReport validate(@Nonnull Request request, @Nonnull ApiOperation apiOperation) {
		List<String> missingFields = new ArrayList<>();
		for (ConditionalJsonPathSupplier conditionalJsonPathSupplier : this.conditionalJsonPathSupplierList) {
			List<PathItem.HttpMethod> apiMethods = Optional.ofNullable(conditionalJsonPathSupplier.getApiMethods())
					.orElseGet(Collections::emptyList);
			List<NormalisedPath> apiPaths = Optional.ofNullable(conditionalJsonPathSupplier.getApiPath())
					.orElseGet(Collections::emptyList);
			if (apiPaths.stream().anyMatch(apiPath -> apiOperation.getApiPath().matches(apiPath))
					&& apiMethods.contains(apiOperation.getMethod())) {
				Optional<com.atlassian.oai.validator.model.Body> body = request.getRequestBody();
				if (body.isPresent()) {
					DocumentContext context = getDocumentContext(request);
					if(validateConditional(missingFields, conditionalJsonPathSupplier, context)) {
						return validationUtils.processMessages(SCHEMA_REQUIRED, SCHEMA_REQUIRED_MESSAGE, missingFields);
					}

				}
			}
		}
		return ValidationReport.empty();
	}

	protected DocumentContext getDocumentContext(Request request) {
		Map<String, Object> requestMap = new LinkedHashMap<>();
		requestMap.put("request", jsonProcessor.jsonToObject(request.getRequestBody().get().toString(), Map.class));
		requestMap.put("headers", request.getHeaders());
		requestMap.put("query", request.getQueryParameters());
		return JsonPath.using(conf).parse(jsonProcessor.toJsonString(requestMap));
	}

	private boolean validateConditional(List<String> missingFields, ConditionalJsonPathSupplier conditionalJsonPathSupplier, DocumentContext context) {
		List<ConditionalMandatory> conditionalMandatories =  conditionalJsonPathSupplier.getConditionalMandatoryPaths();
		if(!CollectionUtils.isEmpty(conditionalMandatories)) {
			conditionalMandatories.forEach(  validator -> {
				if(checkAllConditionsAreValid(validator.getConditions(), context)) {
					 Optional<List<String>> optionalJsonPaths = Optional.ofNullable(validator.getJsonPaths());
					 optionalJsonPaths.ifPresent(jsonPaths ->{
						 missingFields.addAll(validateMandatoryFields(context, missingFields));
					 });
				}
			});
			return true;
		}
		return false;
	}

	protected boolean checkAllConditionsAreValid(List<Condition> conditions, DocumentContext context) {
		if (!CollectionUtils.isEmpty(conditions)) {
			for (Condition condition : conditions) {
				JsonNode value = context.read(condition.getJsonPath(), JsonNode.class);
				;
				if (!Objects.isNull(value) && value.isArray()) {
					log.info("Using first element in values array for the json path {}", condition.getJsonPath());
					value = value.get(0);
				}
				if (Objects.isNull(value) || "null".equals(value.asText())) {
					return false;
				}
				if (Optional.ofNullable(condition.getValues().contains(value.asText())).isPresent()) {
					return false;
				}
			}
		}
		return true;
	}
}
