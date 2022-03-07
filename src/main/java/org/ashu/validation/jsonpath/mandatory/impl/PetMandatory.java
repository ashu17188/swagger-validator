package org.ashu.validation.jsonpath.mandatory.impl;

import java.util.List;

import org.ashu.validation.validator.MandatoryJsonPathSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atlassian.oai.validator.model.NormalisedPath;
import com.atlassian.oai.validator.model.NormalisedPathImpl;

@Component
public class PetMandatory implements MandatoryJsonPathSupplier{

	public static final String PET_POST = "pet";
	
	@Value("${server.servlet.context-path}")
	private String basePath;
	
	@Autowired
	private JsonPaths jsonPaths;
	
	@Override
	public List<NormalisedPath> getApiPath() {
		return List.of(new NormalisedPathImpl(PET_POST, basePath));
	}

	@Override
	public List<String> getJsonPaths() {
		return jsonPaths.getPet();
	}

	
}
