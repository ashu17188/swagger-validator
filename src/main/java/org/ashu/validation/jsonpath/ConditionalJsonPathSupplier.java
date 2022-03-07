package org.ashu.validation.jsonpath;

import java.util.List;

import com.atlassian.oai.validator.model.NormalisedPath;
import com.google.common.collect.Lists;

import io.swagger.v3.oas.models.PathItem;

public interface ConditionalJsonPathSupplier {

	List<NormalisedPath> getApiPath();

	default List<PathItem.HttpMethod> getApiMethods() {
		return Lists.newArrayList(PathItem.HttpMethod.POST, PathItem.HttpMethod.PUT);
	}

	List<ConditionalMandatory> getConditionalMandatoryPaths();
}
