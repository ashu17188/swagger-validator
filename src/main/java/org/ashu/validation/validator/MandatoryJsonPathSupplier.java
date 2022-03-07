package org.ashu.validation.validator;

import java.util.List;

import com.atlassian.oai.validator.model.NormalisedPath;
import com.google.common.collect.Lists;

import io.swagger.v3.oas.models.PathItem;

public interface MandatoryJsonPathSupplier {

	List<NormalisedPath> getApiPath();

	default List<PathItem.HttpMethod> getApiMethods() {
		return Lists.newArrayList(PathItem.HttpMethod.POST, PathItem.HttpMethod.PUT);
	}

	List<String> getJsonPaths();
}
