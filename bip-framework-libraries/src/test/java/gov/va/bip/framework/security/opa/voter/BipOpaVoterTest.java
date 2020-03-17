package gov.va.bip.framework.security.opa.voter;

import gov.va.bip.framework.client.rest.template.RestClientTemplate;
import gov.va.bip.framework.security.jwt.JwtAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.FilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class BipOpaVoterTest {

	@Mock
	private RestClientTemplate restTemplate;
	@InjectMocks
	BipOpaVoter voter = new BipOpaVoter("http://localhost:8181/api/v1/mytest/pid", restTemplate);

	private final ConfigAttribute configAttrib = new SecurityConfig("test_security_config");
	private final Authentication jwtUserToken = new JwtAuthenticationToken("test_token");
	private FilterInvocation filterInvocationWithMethodRequestUri;
	private FilterInvocation filterInvocationNoMethodRequestUri;

	@Before
	public void setup() {
		MockHttpServletRequest requestWithMethodUri = new MockHttpServletRequest("POST", "api/v1/mytest/pid");
		requestWithMethodUri.addHeader("x-real-ip", "127.0.0.1");
		requestWithMethodUri.addParameter("test_name", "test_value");

		MockHttpServletRequest requestWithNoMethodUri = new MockHttpServletRequest(null, null);

		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		filterInvocationWithMethodRequestUri = new FilterInvocation(requestWithMethodUri, response, chain);
		filterInvocationNoMethodRequestUri = new FilterInvocation(requestWithNoMethodUri, response, chain);
	}

	@Test
	public void testPostConstruct() {
		BipOpaVoter voter = new BipOpaVoter("http://localhost:8181/api/v1/mytest/pid", restTemplate);
		voter.postConstruct();
	}

	@Test
	public void nullAuthenticationAbstain() {
		BipOpaVoter voter = new BipOpaVoter("", restTemplate);
		Authentication notAuthenitcated = null;
		assertThat(voter.vote(notAuthenitcated, this, SecurityConfig.createList("A")))
				.isEqualTo(BipOpaVoter.ACCESS_ABSTAIN);
	}

	@Test
	public void notFilterInvocationAbstain() {

		BipOpaDataResponse bipOpaDataResponse = new BipOpaDataResponse();
		bipOpaDataResponse.setResult(false);

		assertThat(voter.vote(jwtUserToken, this, SecurityConfig.createList("A")))
				.isEqualTo(BipOpaVoter.ACCESS_ABSTAIN);
	}

	@Test
	public void authenticationAccessDenied() {

		BipOpaDataResponse bipOpaDataResponse = new BipOpaDataResponse();
		bipOpaDataResponse.setResult(false);

		Mockito.when(restTemplate.postForEntity(ArgumentMatchers.anyString(),
				ArgumentMatchers.<HttpEntity<BipOpaDataRequest>>any(),
				ArgumentMatchers.<Class<BipOpaDataResponse>>any()))
				.thenReturn(new ResponseEntity<>(bipOpaDataResponse, HttpStatus.OK));

		assertThat(voter.vote(jwtUserToken, filterInvocationWithMethodRequestUri, SecurityConfig.createList("A")))
				.isEqualTo(BipOpaVoter.ACCESS_DENIED);
	}

	@Test
	public void authenticationAccessGranted() {

		BipOpaDataResponse bipOpaDataResponse = new BipOpaDataResponse();
		bipOpaDataResponse.setResult(true);

		Mockito.when(restTemplate.postForEntity(ArgumentMatchers.anyString(),
				ArgumentMatchers.<HttpEntity<BipOpaDataRequest>>any(),
				ArgumentMatchers.<Class<BipOpaDataResponse>>any()))
				.thenReturn(new ResponseEntity<>(bipOpaDataResponse, HttpStatus.OK));

		assertThat(voter.vote(jwtUserToken, filterInvocationWithMethodRequestUri, SecurityConfig.createList("A")))
				.isEqualTo(BipOpaVoter.ACCESS_GRANTED);
	}

	@Test
	public void authenticationRequestNoRequestUriAccessGranted() {

		BipOpaDataResponse bipOpaDataResponse = new BipOpaDataResponse();
		bipOpaDataResponse.setResult(true);

		Mockito.when(restTemplate.postForEntity(ArgumentMatchers.anyString(),
				ArgumentMatchers.<HttpEntity<BipOpaDataRequest>>any(),
				ArgumentMatchers.<Class<BipOpaDataResponse>>any()))
				.thenReturn(new ResponseEntity<>(bipOpaDataResponse, HttpStatus.OK));

		assertThat(voter.vote(jwtUserToken, filterInvocationNoMethodRequestUri, SecurityConfig.createList("A")))
				.isEqualTo(BipOpaVoter.ACCESS_GRANTED);
	}

	@Test
	public void testSupportsConfigAttribute() {
		assertTrue(voter.supports(configAttrib));
	}

	@Test
	public void testSupportsClass() {
		assertTrue(voter.supports(String.class));
	}
}
