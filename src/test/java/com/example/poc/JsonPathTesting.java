package com.example.poc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
	
	@Test
	public void test_MinJumps() {
		int[] jumps = {2, 1, 1, 1, 4};
   log.info("Min jumps {}",countMinJumps(jumps));
	}
	
	public int countMinJumps(int[] jumps) {
    int[] dp = new int[jumps.length];

    //initialize with infinity, except the first index which should be zero as we start from there
    for(int i=1; i<jumps.length; i++)
      dp[i] = Integer.MAX_VALUE;

    for(int start=0; start < jumps.length-1; start++) {
      for(int end=start+1; end <= start+jumps[start] && end < jumps.length; end++)
        dp[end] = Math.min(dp[end], dp[start]+1);
    }

    return dp[jumps.length-1];
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
