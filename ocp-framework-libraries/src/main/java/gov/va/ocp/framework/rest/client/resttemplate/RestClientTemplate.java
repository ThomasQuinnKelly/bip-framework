package gov.va.ocp.framework.rest.client.resttemplate;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestClientTemplate {

	/**
	 * REST client class that uses Spring RestTemplate to make service call
	 */
	private RestTemplate restTemplate;

	/**
	 * Create the client template with the default Spring {@link RestTemplate}.
	 * <p>
	 * This type of client RestTemplate is not appropriate for most inter-service operations.</br>
	 * The default template:
	 * <ul>
	 * <li>does <b>not</> derive request timeout values from the application properties
	 * <li>does <b>not</> attach the JWT from the existing session to the outgoing request
	 * <li>does <b>not</> attach any interceptors to the request/response cycle
	 * </ul>
	 */
	public RestClientTemplate() {
		this.restTemplate = new RestTemplate();
	}

	/**
	 * Create the client template with a pre-configured {@link RestTemplate}.
	 * <p>
	 * For most inter-service calls, the pre-configured template should:
	 * <ul>
	 * <li>derive request timeout values from the application properties
	 * <li>attach the JWT from the existing session to the outgoing request
	 * <li>optionally attach interceptors or other configuration options to the request/response cycle
	 * </ul>
	 *
	 * @param restTemplate the pre-configured RestTemplate
	 */
	public RestClientTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Simplified method to send a request using
	 * {@link RestTemplate#exchange(java.net.URI, HttpMethod, org.springframework.http.HttpEntity, ParameterizedTypeReference)
	 *
	 * @param <T> the generic type
	 * @param url the URL
	 * @param methodType the method type
	 * @param requestEntity the request entity
	 * @param responseType the {@link java.lang.reflect.Type} to return
	 * @return ResponseEntity a {@link ResponseEntity} of {@code responseType}
	 */
	public <T> ResponseEntity<T> executeURL(String url, HttpMethod methodType, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {
		return this.restTemplate.exchange(url, methodType, requestEntity, responseType);
	}
	

	/**
	 * Post for entity.
	 *
	 * @param <T> the generic type
	 * @param url the url
	 * @param methodType the method type
	 * @param requestEntity the request entity
	 * @param responseType the response type
	 * @return the response entity
	 */
	public <T> ResponseEntity<T> postForEntity(String url, HttpEntity<?> requestEntity, Class<T> responseType) {
		return this.restTemplate.postForEntity(url, requestEntity, responseType);
	}
}
