package org.ashu.schema.validation.jsonpath.mandatory;


import org.ashu.schema.validation.GenericRequestValidator;
import org.springframework.stereotype.Service;

import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.ValidationReport;

@Service
public class JsonPathMandatoryValidator implements GenericRequestValidator{

	
	@Override
	public ValidationReport validate(Request request, ApiOperation apiOperation) {
		return null;
	}

}
