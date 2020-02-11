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
 * The Class OPAVoter.
 */
public class OPAVoter implements AccessDecisionVoter<Object> {
	
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(OPAVoter.class);

	private String opaUrl;

	/**
	 * Instantiates a new OPA voter.
	 *
	 * @param opaUrl the opa url
	 */
	public OPAVoter(String opaUrl) {
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
	 * Vote.
	 *
	 * @param authentication the Authentication
	 * @param obj the FilterInvocation object
	 * @param attrs the ConfigAttribute
	 * @return the int Indicates whether or not access is granted.
	 */
	@Override
	public int vote(Authentication authentication, Object obj, Collection<ConfigAttribute> attrs) {
		
		LOGGER.debug("Open Policy Agent URL {}", opaUrl);

		if (!(obj instanceof FilterInvocation)) {
			return ACCESS_ABSTAIN;
		}

		FilterInvocation filter = (FilterInvocation) obj;
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
		HttpEntity<?> request = new HttpEntity<>(new OPADataRequest(input));
		ResponseEntity<OPADataResponse> response = client.postForEntity(this.opaUrl, request, OPADataResponse.class);

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
	protected String extractHeaderToken(HttpServletRequest request) {
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
