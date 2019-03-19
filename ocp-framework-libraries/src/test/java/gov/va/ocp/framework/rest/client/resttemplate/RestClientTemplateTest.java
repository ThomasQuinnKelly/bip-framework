package gov.va.ocp.framework.rest.client.resttemplate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.ocp.framework.rest.client.resttemplate.RestClientTemplate;
import gov.va.ocp.framework.service.DomainResponse;

public class RestClientTemplateTest {

	private static final String DUMMY_ENDPOINT = "https://jsonplaceholder.typicode.com/posts/1";
	private ParameterizedTypeReference<DomainResponse> responseType = new ParameterizedTypeReference<DomainResponse>() {
	};
	private ResponseEntity<DomainResponse> responseEntity;
	private RestClientTemplate restClientTemplate;
	private AnnotationConfigWebApplicationContext context;

	@Before
	public void setUp() throws Exception {
		context = new AnnotationConfigWebApplicationContext();
		context.register(TestRestAutoConfiguration.class);
		context.refresh();
	}

	@After
	public void tearDown() throws Exception {
		context.close();
	}

	@Test
	public void testRestClientTemplateWithParam() {
		RestClientTemplate restClientTemplateWithParam = (RestClientTemplate) this.context.getBean("restClientTemplateWithParam");
		assertNotNull(restClientTemplateWithParam);
	}

	@Test
	public void testExecuteURL() {
		restClientTemplate = this.context.getBean("restClientTemplate", RestClientTemplate.class);
		assertNotNull(restClientTemplate);
		try {
			setResponseEntity(restClientTemplate.executeURL(DUMMY_ENDPOINT, HttpMethod.GET, null, responseType));
		} catch (Exception e) {
			System.out.println("REST call is expected to fail");
			assertTrue(e instanceof RestClientException);
		}
	}

	public ResponseEntity<DomainResponse> getResponseEntity() {
		return responseEntity;
	}

	public void setResponseEntity(ResponseEntity<DomainResponse> responseEntity) {
		this.responseEntity = responseEntity;
	}
}
