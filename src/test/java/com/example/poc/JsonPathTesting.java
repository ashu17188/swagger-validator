package com.example.poc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.ashu.schema.validation.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

public class JsonPathTesting {

			
	
	@DisplayName("Test JsonPath")
	@Test
	public void testJsonPath() {
		String jsonDataSourceString = TestUtils.readFileToString("jsonPath.json", "request");
		Filter expensiveFilter = Filter.filter(Criteria.where("director").eq("Marc Forster"));
		List<Map<String, Object>> expensive = JsonPath.parse(jsonDataSourceString)
		  .read("$[0].director", expensiveFilter);
		assertNotNull(expensive);
	}
	
  
  }

