package org.ashu.validation.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonProcessor {

	private final ObjectMapper objectMapper;

	public JsonProcessor(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	public String toJsonString(Object o) {
		try {
			return this.objectMapper.writeValueAsString(o);
		} catch (JsonProcessingException exp) {
			throw new RuntimeException("Unable to parse json");
		}
	}

	public <T> T readValue(String content, Class<T> type) {
		try {
			return this.objectMapper.readValue(content, type);
		} catch (Exception e) {
			throw new RuntimeException("Unable to create object from json");
		}
	}
	
	public <T> T readValue(String content, TypeReference<T> valueTypeRef) {
		try {
			return this.objectMapper.readValue(content, valueTypeRef);
		} catch (Exception e) {
			throw new RuntimeException("Unable to create object from json");
		}
	}

	public <T> T jsonNodeToObject(JsonNode jsonNode, Class<T> type) {
		return this.objectMapper.convertValue(jsonNode, type);
	}
}
