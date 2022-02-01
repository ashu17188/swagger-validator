package org.ashu.validation.jsonpath.mandatory.impl;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("json-paths.mandatory")
public class JsonPaths {

	private List<String> pet;

	public List<String> getPet() {
		return pet;
	}

	public void setPet(List<String> pet) {
		this.pet = pet;
	}

}
