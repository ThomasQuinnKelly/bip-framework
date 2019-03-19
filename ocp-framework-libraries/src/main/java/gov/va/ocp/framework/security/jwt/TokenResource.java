package gov.va.ocp.framework.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.va.ocp.framework.security.model.Person;
import gov.va.ocp.framework.security.util.GenerateToken;
import gov.va.ocp.framework.swagger.SwaggerResponseMessages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class TokenResource implements SwaggerResponseMessages {

	private static final String API_OPERATION_VALUE = "Get JWT Token";
	private static final String API_OPERATION_NOTES = "Get a JWT bearer token with 'person' data. "
			+ "Include MVI correlationIds if required by the target API.";
	private static final String API_PARAM_GETTOKEN_PERSON = "Identity information for the authenticated user. "
			+ "CorrelationIds may be null or an empty array if the target API does not require it. "
			+ "Otherwise, correlationIds must be the list as retrieved from MVI:\n"
			+ "<table style=\"table-layout:auto;width:700px;text-align:left;background-color:#efefef;\">"
			+ "<tr><th>Common ID Name</th><th>Example ID</th><th>Type</th><th>Source</th><th>Issuer</th><th>Status</th><th </tr>"
			+ "<tr><td>Participant ID (PID)</td><td>12345678</td><td>PI</td><td>200CORP</td><td>USVBA</td><td>A</td></tr>"
			+ "<tr><td>File Number</td><td>123456789</td><td>PI</td><td>200BRLS</td><td>USVBA</td><td>A</td></tr>"
			+ "<tr><td>ICN</td><td>1234567890V123456</td><td>NI</td><td>200M</td><td>USVHA</td><td>A</td></tr>"
			+ "<tr><td>EDIPI / PNID</td><td>1234567890</td><td>NI</td><td>200DOD</td><td>USDOD</td><td>A</td></tr>"
			+ "<tr><td>SSN</td><td>123456789</td><td>SS</td><td></td><td></td><td></td></tr>"
			+ "</table>";

	@Autowired
	private JwtAuthenticationProperties jwtAuthenticationProperties;

	@Value("${ocp.security.jwt.validation.required-parameters:}")
	private String[] jwtTokenRequiredParameterList;

	@RequestMapping(value = "/token", method = RequestMethod.POST, consumes = { "application/json" })
	@ApiOperation(value = API_OPERATION_VALUE, notes = API_OPERATION_NOTES)
	@ApiResponses(value = { @ApiResponse(code = 200, message = MESSAGE_200), @ApiResponse(code = 400, message = MESSAGE_400),
			@ApiResponse(code = 500, message = MESSAGE_500) })
	public String getToken(
			@ApiParam(value = API_PARAM_GETTOKEN_PERSON, required = true) @RequestBody final Person person) {
		// @ApiModel(description="Identity information for the authenticated user.")
		return GenerateToken.generateJwt(person, jwtAuthenticationProperties.getExpireInSeconds(),
				jwtAuthenticationProperties.getSecret(), jwtAuthenticationProperties.getIssuer(), jwtTokenRequiredParameterList);
	}

	/**
	 * Registers fields that should be allowed for data binding.
	 *
	 * @param binder
	 *            Spring-provided data binding context object.
	 */
	@InitBinder
	public void initBinder(final WebDataBinder binder) {
		binder.setAllowedFields("birthDate", "firstName", "lastName", "middleName", "prefix", "suffix",
				"gender", "assuranceLevel", "email", "dodedipnid", "pnidType", "pnid", "pid", "icn", "fileNumber",
				"tokenId", "correlationIds");
	}
}
