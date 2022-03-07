package org.ashu.validation.jsonpath;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Condition {

	private String jsonPath;
	
	private List<String> values;
	

}
