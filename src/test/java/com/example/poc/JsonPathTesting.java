package com.example.poc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.ashu.validation.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonPathTesting {

	@DisplayName("Test JsonPath")
	@Test
	public void testJsonPath() {
		String jsonDataSourceString = TestUtils.readFileToString("jsonPath.json", "request");
		Filter expensiveFilter = Filter.filter(Criteria.where("director").eq("Marc Forster"));
		List<Map<String, Object>> expensive = JsonPath.parse(jsonDataSourceString).read("$[0].director", expensiveFilter);
		assertNotNull(expensive);
	}

	@DisplayName("Test Pattern matching")
	@Test
	public void test_pattern_match() {
		boolean match = Pattern.matches("\\dx\\d", "4	x1");
		assertTrue(match);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@DisplayName("Test trace path")
	@Test
	public void test_tracePath() {
		int[] arr = {1,2,3,4,5,6,7,8,9};
		maxMin(arr);
		log.info("max-min array: {}", arr);
	}
	
  public static void maxMin(int[] arr) {
    int maxIdx = arr.length - 1;
    int minIdx = 0;
    int maxElem = arr[maxIdx] + 1; // store any element that is greater than the maximum element in the array 
    for( int i = 0; i < arr.length; i++) {
      // at even indices we will store maximum elements
      if (i % 2 == 0){  
        arr[i] += (arr[maxIdx] % maxElem ) * maxElem;
        maxIdx -= 1;
      }
      else { // at odd indices we will store minimum elements
        arr[i] += (arr[minIdx] % maxElem ) * maxElem;
        minIdx += 1;
      }
    }
    // dividing with maxElem to get original values.
    for( int i = 0; i < arr.length; i++) {
      arr[i] = arr[i] / maxElem;
    }
  }
}
