import javax.annotation.PostConstruct;

import org.ashu.validation.jsonpath.ConditionalMandatory;
import org.ashu.validation.util.JsonProcessor;
import org.ashu.validation.utils.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Component
public class ProposalConditionalMandatoryJsonPathSupplier {

	public static final String PROPOSAL_POST = "/api/v1/proposals";

	@Value("${server.servlet.context-path}")
	private String basePath;

	@Autowired
	private JsonProcessor jsonProcessor;

	private ConditionalMandatory[] jsonPaths;

	@PostConstruct
	public void init() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();
		try {
			jsonPaths = mapper.readValue(TestUtils.readFileToString("conditional-mandatory-json-path.yml", ""),
					new TypeReference<ConditionalMandatory[]>() {
					});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
