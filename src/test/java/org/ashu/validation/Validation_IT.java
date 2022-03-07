package org.ashu.validation;

import org.ashu.validation.test.TestConfig;
import org.ashu.validation.util.ValidationUtils;
import org.generated.models.BadRequestField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
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
	void testPostProposalwithConditionalMandatory() {
		BadRequestField badRequestField = new BadRequestField();
		badRequestField.setNameOfField("approvedBy == tester");
		badRequestField.setUrn("body:$.request.discount[?(@.approvedBy == 'tester')]");
		badRequestField.setMessage("Object has missing required properties in path");

		MockHttpServletRequestBuilder  mockHttpServletRequestBuilder =  
				MockMvcRequestBuilders.post("/api/v1/proposals")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getProposalPayload("/pet-conditional-mandatory-post-badrequest.json"))
				.header("app-id", "test-app")
				.header("Authorization", "test-auth")
				.header("session-id", "test-session")
				.header("request-timestamp", "test-time")
				.header("client-id", "test-id")
				.header("workflow_status", "1")
				.header("user-id", "test");
		
	}
	
	private String getProposalPayload(String jsonResourcePath) throws JsonProcessingException{
		Proposal proposal = validationUtils.readJsonResource(jsonResourcePath, Proposal.class);
		return objectMapper.writeValueAsString(proposal);
	}
}
