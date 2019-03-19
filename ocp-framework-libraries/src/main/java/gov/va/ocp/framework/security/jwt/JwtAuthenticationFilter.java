package gov.va.ocp.framework.security.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.AuditLogger;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

/**
 * Configure springboot starter for the platform.
 * Similar to {@code UsernamePasswordAuthenticationFilter}
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private JwtAuthenticationProperties jwtAuthenticationProperties;

	private static final OcpLogger LOG = OcpLoggerFactory.getLogger(JwtAuthenticationFilter.class);

	/**
	 * Create the filter.
	 *
	 * @param jwtAuthenticationProperties
	 * @param jwtAuthenticationSuccessHandler
	 * @param jwtAuthenticationProvider
	 */
	public JwtAuthenticationFilter(JwtAuthenticationProperties jwtAuthenticationProperties,
			AuthenticationSuccessHandler jwtAuthenticationSuccessHandler,
			AuthenticationProvider jwtAuthenticationProvider) {
		super(new AuthenticationRequestMatcher(jwtAuthenticationProperties.getFilterProcessUrls()));
		this.jwtAuthenticationProperties = jwtAuthenticationProperties;
		setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
		setAuthenticationManager(new ProviderManager(new ArrayList<>(Arrays.asList(jwtAuthenticationProvider))));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String token = request.getHeader(jwtAuthenticationProperties.getHeader());
		if (token == null || !token.startsWith("Bearer ")) {
			LOG.error("No JWT Token in Header");
			throw new JwtAuthenticationException("No JWT Token in Header");
		}

		token = token.substring(7);

		try {
			return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
		} catch (SignatureException signatureException) {
			writeAuditForJwtTokenErrors(new StringBuffer("Tampered Token[").append(token).append("]\nSignatureException[")
					.append(signatureException.getMessage()).append("]\n").toString(), request, signatureException);
			throw new JwtAuthenticationException("Tampered Token");
		} catch (MalformedJwtException ex) {
			writeAuditForJwtTokenErrors(new StringBuffer("Malformed Token[").append(token).append(" ]\nMalformedJwtException[")
					.append(ex.getMessage()).append("]\n").toString(), request, ex);
			throw new JwtAuthenticationException("Malformed Token");
		}
	}

	/**
	 * Audit any errors.
	 *
	 * @param cause - cause
	 * @param request - original request
	 */
	private void writeAuditForJwtTokenErrors(final String cause, final HttpServletRequest request, final Throwable t) {
		String message = "";
		try {
			message = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOG.error("Error while reading the request {}", e);
		}

		String data = cause.concat(" Request: ").concat(message);
		AuditEventData auditData =
				new AuditEventData(AuditEvents.SECURITY, "attemptAuthentication", JwtAuthenticationFilter.class.getName());
		AuditLogger.error(auditData, data, t);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#successfulAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain, org.springframework.security.core.Authentication)
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);

		chain.doFilter(request, response);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#unsuccessfulAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
	  
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getLocalizedMessage()); 
	}
}

/**
 * Set rules for what requests should be ignored.
 */
class AuthenticationRequestMatcher implements RequestMatcher {
	private RequestMatcher baselineMatches;

	/**
	 * Requests to ignore based on ant path configurations.
	 *
	 * @param baselineMatches
	 * @param ignoreUrls
	 */
	public AuthenticationRequestMatcher(String[] baselineMatches) {
		this.baselineMatches = authMatchers(baselineMatches);
	}

	/**
	 * Requests to ignore based on ant path configurations.
	 *
	 * @param baselineMatches
	 * @param ignoreMatches
	 */
	public AuthenticationRequestMatcher(RequestMatcher baselineMatches) {
		this.baselineMatches = baselineMatches;
	}

	/**
	 * Add exclusion URLs to the list.
	 *
	 * @param exclusionUrls
	 * @return RequestMatcher
	 */
	private RequestMatcher authMatchers(String[] authUrls) {
		LinkedList<RequestMatcher> matcherList = new LinkedList<>();
		for (String url : authUrls) {
			matcherList.add(new AntPathRequestMatcher(url));
		}
		return new OrRequestMatcher(matcherList);
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		return baselineMatches.matches(request);
	}
}