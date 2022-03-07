package org.ashu.validation.jsonpath;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConditionalMandatory {
	
	private List<Condition> conditions;
	
	private List<String> jsonPaths;
	
}
