package org.ashu.validation;

import org.springframework.stereotype.Component;

@Component
public class TestApiSpecsSupplier implements OpenApiSpecsSupplier{

	@Override
	public String getOpenApiSecsUrl() {
		return "api/openapi-spec-test.yml";
	}
}
