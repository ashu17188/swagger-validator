package org.ashu.validation.test;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.generated.models.Pet;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value="/api/v1")
public class TestController {


	@PostMapping("/pet")
	@ResponseStatus(HttpStatus.CREATED)
	public void savePet(HttpServletRequest request, @RequestBody @NotNull Pet pet	) {
		log.info("POST: save pet called");
	}
	
	@PutMapping("/pet/{pet_id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateProposal(HttpServletRequest request, @RequestBody @NotNull Pet pet) {}
}
