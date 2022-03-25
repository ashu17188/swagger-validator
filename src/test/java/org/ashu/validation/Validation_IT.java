package org.ashu.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.ashu.validation.test.TestConfig;
import org.ashu.validation.util.ValidationUtils;
import org.ashu.validation.utils.TestUtils;
import org.generated.models.BadRequestField;
import org.generated.models.Pet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
@WebAppConfiguration
@SpringBootTest(classes = {TestConfig.class})
@AutoConfigureMockMvc
public class Validation_IT {

	@Autowired
	private ValidationUtils validationUtils;
	
	@Autowired
	ObjectMapper objectMapper;

	@DisplayName("Conditional Mandatory: Throws BadRequest")
	@Test
	void testPostProposalwithConditionalMandatory(@Autowired MockMvc mockMvc) throws Exception {
		BadRequestField badRequestField = new BadRequestField();
		badRequestField.setNameOfField("approvedBy == tester");
		badRequestField.setUrn("body:$.request.discount[?(@.approvedBy == 'tester')]");
		badRequestField.setMessage("Object has missing required properties in path");

		//context path: /api/swagger-validation-poc/v1
		MockHttpServletRequestBuilder  mockHttpServletRequestBuilder =  
				MockMvcRequestBuilders.post("/api/v1/pet")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getProposalPayload("/pet-conditional-mandatory-post-badrequest.json"))
				.header("app-id", "test-app")
				.header("Authorization", "test-auth")
				.header("session-id", "test-session")
				.header("request-timestamp", "test-time")
				.header("client-id", "test-id")
				.header("workflow_status", "1")
				.header("user-id", "test");
		MvcResult result = mockMvc.perform(mockHttpServletRequestBuilder)
//				.andDo(print())
				.andExpect(status().isCreated())
				.andReturn();
		assertNotNull(result.getResponse());
		ObjectMapper objectMapper = new ObjectMapper();
		BadRequest badRequest = objectMapper.readValue(result.getResponse().getContentAsString(), BadRequest.class); 
		assertNotNull(badRequest);
		assertTrue(badRequest.equals(badRequestField.getNameOfField()));
	}
	
	private String getProposalPayload(String jsonResourcePath) throws JsonProcessingException{
		return TestUtils.readFileToString(jsonResourcePath, "request");
	}
}
