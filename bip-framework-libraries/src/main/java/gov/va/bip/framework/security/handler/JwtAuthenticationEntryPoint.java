package gov.va.bip.framework.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.rest.provider.ProviderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Implement custom {@link AuthenticationEntryPoint}, our own authentication
 * entry point, we can tell Spring Security exactly what to do if someone tries
 * to access a protected resource without being authenticated.
 * 
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.web.AuthenticationEntryPoint#commence(javax.
	 * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		ProviderResponse body = new ProviderResponse();
		body.addMessage(MessageSeverity.ERROR, HttpStatus.UNAUTHORIZED.name(), authException.getMessage(),
				HttpStatus.UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);
		response.getOutputStream().println(mapper.toString());
	}
}
