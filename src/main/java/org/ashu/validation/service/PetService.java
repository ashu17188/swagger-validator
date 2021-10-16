package org.ashu.validation.service;

import java.util.List;

import org.ashu.java.api.PetApiDelegate;
import org.ashu.validation.util.JsonProcessor;
import org.ashu.validation.util.ResourceUtils;
import org.generated.models.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class PetService implements PetApiDelegate {

	@Autowired
	private JsonProcessor jsonProcessor;

	@Override
	public ResponseEntity<List<Pet>> getAllPetsUsingGET(Integer page, Integer size, String sort) {
		String getResponse = ResourceUtils.readFileToString("getAllPetsResponse.json", "response");
		List<Pet> pets = jsonProcessor.readValue(getResponse, new TypeReference<List<Pet>>() {
		});

		return new ResponseEntity<>(pets, HttpStatus.OK);

	}
}
