package gov.va.bip.framework.security.opa.voter;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import gov.va.bip.framework.client.rest.template.RestClientTemplate;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

/**
 * The Class BipOpaVoter.
 */
public class BipOpaVoter implements AccessDecisionVoter<Object> {
	
	/** The Constant LOGGER. */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipOpaVoter.class);

	/** The OPA URL. */
	private String opaUrl;

	/**
	 * Instantiates a new OPA voter.
	 *
	 * @param opaUrl the opa url
	 */
	public BipOpaVoter(String opaUrl) {
		this.opaUrl = opaUrl;
	}

	/**
	 * Supports.
	 *
	 * @param attribute the attribute
	 * @return true, if successful
	 */
	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	/**
	 * Supports.
	 *
	 * @param clazz the clazz
	 * @return true, if successful
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	/**
	 * Indicates whether or not access is granted.
	 * <p>
	 * The decision must be affirmative ({@code ACCESS_GRANTED}), negative (
	 * {@code ACCESS_DENIED}) or the {@code AccessDecisionVoter} can abstain (
	 * {@code ACCESS_ABSTAIN}) from voting. 
	 * <p>
	 * Unless an {@code AccessDecisionVoter} is specifically intended to vote on an access
	 * control decision due to a passed method invocation or configuration attribute
	 * parameter, it must return {@code ACCESS_ABSTAIN}. This prevents the coordinating
	 * {@code AccessDecisionManager} from counting votes from those
	 * {@code AccessDecisionVoter}s without a legitimate interest in the access control
	 * decision.
	 *
	 * @param authentication the caller making the invocation
	 * @param object the secured object being invoked
	 * @param attributes the configuration attributes associated with the secured object
	 *
	 * @return either {@link #ACCESS_GRANTED}, {@link #ACCESS_ABSTAIN} or
	 * {@link #ACCESS_DENIED}
	 */
	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		
		LOGGER.debug("Open Policy Agent URL {}", opaUrl);

		if (!(object instanceof FilterInvocation)) {
			return ACCESS_ABSTAIN;
		}

		FilterInvocation filter = (FilterInvocation) object;
		Map<String, String> headers = new HashMap<>();

		headers.put("jwtToken", extractHeaderToken(filter.getRequest()));

		for (Enumeration<String> headerNames = filter.getRequest().getHeaderNames(); headerNames.hasMoreElements();) {
			String header = headerNames.nextElement();
			headers.put(header, filter.getRequest().getHeader(header));
		}

		String[] path = filter.getRequest().getRequestURI().replaceAll("^/|/$", "").split("/");

		Map<String, Object> input = new HashMap<>();
		input.put("auth", authentication);
		input.put("method", filter.getRequest().getMethod());
		input.put("path", path);
		input.put("headers", headers);

		RestClientTemplate client = new RestClientTemplate();
		HttpEntity<?> request = new HttpEntity<>(new BipOpaDataRequest(input));
		ResponseEntity<BipOpaDataResponse> response = client.postForEntity(this.opaUrl, request, BipOpaDataResponse.class);

		if (response.hasBody() && !response.getBody().getResult()) {
			return ACCESS_DENIED;
		}

		return ACCESS_GRANTED;
	}

	/**
	 * Extract the JWT bearer token from a header.
	 * 
	 * @param request The request.
	 * @return The token, or null if no JWT authorization header was supplied.
	 */
	private String extractHeaderToken(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaders("Authorization");
		while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
			String value = headers.nextElement();
			if ((value.toLowerCase().startsWith("Bearer ".toLowerCase()))) {
				String authHeaderValue = value.substring("Bearer ".length()).trim();
				int commaIndex = authHeaderValue.indexOf(',');
				if (commaIndex > 0) {
					authHeaderValue = authHeaderValue.substring(0, commaIndex);
				}
				return authHeaderValue;
			}
		}

		return null;
	}

}
