package gov.va.bip.framework.security.opa.voter;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private RestClientTemplate opaRestTemplate;

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
	 * Unless an {@code AccessDecisionVoter} is specifically intended to vote on an
	 * access control decision due to a passed method invocation or configuration
	 * attribute parameter, it must return {@code ACCESS_ABSTAIN}. This prevents the
	 * coordinating {@code AccessDecisionManager} from counting votes from those
	 * {@code AccessDecisionVoter}s without a legitimate interest in the access
	 * control decision.
	 *
	 * @param authentication the caller making the invocation
	 * @param object         the secured object being invoked
	 * @param attributes     the configuration attributes associated with the
	 *                       secured object
	 *
	 * @return either {@link #ACCESS_GRANTED}, {@link #ACCESS_ABSTAIN} or
	 *         {@link #ACCESS_DENIED}
	 */
	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {

		LOGGER.debug("Open Policy Agent URL {}", opaUrl);

		if (authentication == null || !(object instanceof FilterInvocation)) {
			return ACCESS_ABSTAIN;
		}

		FilterInvocation filter = (FilterInvocation) object;
		HttpServletRequest httpServletRequest = filter.getRequest();

		Map<String, Object> input = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		Map<String, Object> parameters = new HashMap<>();
		String[] path = ArrayUtils.EMPTY_STRING_ARRAY;
		
		if(httpServletRequest.getRequestURI() != null){
			path = httpServletRequest.getRequestURI().replaceAll("^/|/$", "").split("/");
		}

		for (Enumeration<String> headerNames = httpServletRequest.getHeaderNames(); headerNames.hasMoreElements();) {
			String header = headerNames.nextElement();
			headers.put(header, httpServletRequest.getHeader(header));
		}

		for (Enumeration<String> parameterNames = httpServletRequest.getParameterNames(); parameterNames
				.hasMoreElements();) {
			String parameterName = parameterNames.nextElement();
			parameters.put(parameterName, httpServletRequest.getParameter(parameterName));
		}

		// populate input HashMap
		input.put("auth", authentication);
		input.put("method", httpServletRequest.getMethod());
		input.put("path", path);
		input.put("headers", headers);
		input.put("parameters", parameters);

		// create the rest client template
		HttpEntity<?> request = new HttpEntity<>(new BipOpaDataRequest(input));
		ResponseEntity<BipOpaDataResponse> response = opaRestTemplate.postForEntity(this.opaUrl, request,
				BipOpaDataResponse.class);

		if (response.hasBody() && !response.getBody().getResult()) {
			return ACCESS_DENIED;
		}

		return ACCESS_GRANTED;
	}
}
