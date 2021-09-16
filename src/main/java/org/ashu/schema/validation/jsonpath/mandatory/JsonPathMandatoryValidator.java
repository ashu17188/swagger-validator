package org.ashu.schema.validation.jsonpath.mandatory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.ashu.schema.validation.GenericRequestValidator;
import org.ashu.schema.validation.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.NormalisedPath;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.ValidationReport;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;

import io.swagger.v3.oas.models.PathItem;

@Service
public class JsonPathMandatoryValidator implements GenericRequestValidator {

	public static final String SCHEMA_REQUIRED = "validation.request.body.schema.required.json-path";
	public static final String SCHEMA_REQUIRED_MESSAGE = "Object has missing required properties in path ([\"%s\"])";

	@Autowired
	private List<MandatoryJsonPathSupplier> mandatoryJsonPathSuppliers;

	@Autowired
	private Configuration conf;

	@Autowired
	private ValidationUtils validationUtils;

	@Override
	public ValidationReport validate(Request request, ApiOperation apiOperation) {
		List<MandatoryJsonPathSupplier> jsonPathsSuppliers = Optional.ofNullable(this.mandatoryJsonPathSuppliers)
				.orElseGet(Collections::emptyList);
		List<String> missingFields = new ArrayList<>();
		for (MandatoryJsonPathSupplier mandatoryJsonPathSupplier : jsonPathsSuppliers) {
			List<PathItem.HttpMethod> apiMethods = Optional.ofNullable(mandatoryJsonPathSupplier.getApiMethods())
					.orElseGet(Collections::emptyList);
			List<NormalisedPath> apiPaths = Optional.ofNullable(mandatoryJsonPathSupplier.getApiPath())
					.orElseGet(Collections::emptyList);
			if (apiPaths.stream().anyMatch(apiPath -> apiOperation.getApiPath().matches(apiPath))
					&& apiMethods.contains(apiOperation.getMethod())) {
				if (request.getRequestBody().isPresent()) {
					DocumentContext context = JsonPath.using(conf).parse(request.getRequestBody().get());
					missingFields.addAll(validateMandatoryFields(context, missingFields));
				}
			}
		}
		return validationUtils.processMessage(SCHEMA_REQUIRED_MESSAGE, SCHEMA_REQUIRED, missingFields);
	}

	public static List<String> validateMandatoryFields(DocumentContext context, List<String> mandatoryJsonPaths) {
		List<String> missingFields = new ArrayList<>();
		Optional.ofNullable(mandatoryJsonPaths).orElseGet(Collections::emptyList);
		for (String jsonPath : mandatoryJsonPaths) {
			JsonNode value = getJsonNode(context, jsonPath);
			if (ObjectUtils.isEmpty(value) & value.isArray() & value.size() > 1) {
				int counter = 0;
				for (Iterator<JsonNode> jsonNodeIterator = value.elements(); jsonNodeIterator.hasNext(); counter++) {
					validateNodeMandatory(jsonNodeIterator.next(), getErrorMessageArrayJsonPath(jsonPath, counter),
							missingFields);
				}
			} else {
				if (!ObjectUtils.isEmpty(value) && value.isArray() & value.size() == 1) {
					value = value.get(0);
				}
				validateNodeMandatory(value, jsonPath, missingFields);
			}
		}
		return missingFields;
	}

	public static JsonNode getJsonNode(DocumentContext context, String jsonPath) {
		JsonNode value = null;
		try {
			value = context.read(jsonPath, JsonNode.class);
		} catch (InvalidPathException e) {
			throw new IllegalArgumentException(String.format("Unable to read path: %s due to %s", jsonPath, e.getMessage()));
		}
		return value;
	}

	public static String getErrorMessageArrayJsonPath(String jsonPath, int counter) {
		return new StringBuilder().append(jsonPath).append(":").append(counter).toString();
	}

	public static void validateNodeMandatory(JsonNode value, String JsonPath, List<String> missingFields) {
		if (ObjectUtils.isEmpty(value) || value.isNull() || (value.isArray() && value.size() == 0)
				|| (value.isValueNode() && StringUtils.isEmpty(value.asText()))) {
			missingFields.add(JsonPath);
		}

	}
}
