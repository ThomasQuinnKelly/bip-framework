package gov.va.bip.framework.security.jwt;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.AuditEvents;
import gov.va.bip.framework.audit.AuditLogger;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

/**
 * Configure springboot starter for the platform.
 * Similar to {@code UsernamePasswordAuthenticationFilter}
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final BipLogger LOG = BipLoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	private JwtAuthenticationProperties jwtAuthenticationProperties;
	
	@Autowired
	private AuthenticationEntryPoint jwtAuthenticationEntryPoint;

	private static final String TOKEN_TAMPERED = "Tampered Token";
	private static final String TOKEN_MALFORMED = "Malformed Token";

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
			MessageKeys key = MessageKeys.BIP_SECURITY_TOKEN_BLANK;
			LOG.error(key.getMessage());
			JwtAuthenticationException authException = new JwtAuthenticationException(key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
			jwtAuthenticationEntryPoint.commence(request, response, authException);
			return null;
		}

		token = token.substring(7);

		try {
			return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
		} catch (SignatureException se) {
			MessageKey key = MessageKeys.BIP_SECURITY_TOKEN_BROKEN;
			String[] params = new String[] { TOKEN_TAMPERED, token, se.getClass().getSimpleName(), se.getMessage() };
			writeAuditForJwtTokenErrors(key.getMessage(params), request, se);
			JwtAuthenticationException authException = new JwtAuthenticationException(key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, se, params);
			jwtAuthenticationEntryPoint.commence(request, response, authException);
			return null;
		} catch (MalformedJwtException ex) {
			MessageKey key = MessageKeys.BIP_SECURITY_TOKEN_BROKEN;
			String[] params = new String[] { TOKEN_MALFORMED, token, ex.getClass().getSimpleName(), ex.getMessage() };
			writeAuditForJwtTokenErrors(key.getMessage(params), request, ex);
			JwtAuthenticationException authException = new JwtAuthenticationException(key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, ex, params);
			jwtAuthenticationEntryPoint.commence(request, response, authException);
			return null;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#successfulAuthentication(javax.servlet.
	 * http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain,
	 * org.springframework.security.core.Authentication)
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);

		chain.doFilter(request, response);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#unsuccessfulAuthentication(javax.servlet.
	 * http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
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