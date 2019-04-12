package gov.va.bip.framework.client.ws;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.xml.soap.SOAPException;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import gov.va.bip.framework.client.ws.BaseWsClientConfig;
import gov.va.bip.framework.exception.BipPartnerRuntimeException;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.PerformanceLogMethodInterceptor;
import gov.va.bip.framework.security.VAServiceWss4jSecurityInterceptor;

@RunWith(MockitoJUnitRunner.class)
public class BaseWsClientConfigTest {

	private Resource KEYSTORE = new FileSystemResource("src/test/resources/ssl/dev/vaebnweb1Keystore.jks");
	private String KEYSTORE_PASS = "password";
	private Resource TRUSTSTORE = new FileSystemResource("src/test/resources/ssl/dev/vaebnTruststore.jks");
	private String TRUSTSTORE_PASS = "password";

	@Mock
	Marshaller mockMarshaller;
	@Mock
	Unmarshaller mockUnmarshaller;
	@Mock
	ClientInterceptor mockClientInterceptor;
	ClientInterceptor intercpetors[] = { mockClientInterceptor };
	@Mock
	HttpResponseInterceptor mockHttpResponseInterceptor;
	HttpResponseInterceptor respInterceptors[] = { mockHttpResponseInterceptor };
	@Mock
	HttpRequestInterceptor mockHttpRequestInterceptor;
	HttpRequestInterceptor reqInterceptors[] = { mockHttpRequestInterceptor };
	@Mock
	WebServiceMessageFactory mockWebServiceMessageFactory;
	@Mock
	Resource mockResource;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateDefaultWebServiceTemplateStringIntIntMarshallerUnmarshaller() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		assertTrue(test.createDefaultWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller,
				mockUnmarshaller) instanceof WebServiceTemplate);
	}

	@Test
	public void testCreateDefaultWebServiceTemplateStringIntIntMarshallerUnmarshallerClientInterceptorArray() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		assertTrue(test.createDefaultWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller, mockUnmarshaller,
				intercpetors) instanceof WebServiceTemplate);
	}

	@Test
	public void
	testCreateDefaultWebServiceTemplateStringIntIntMarshallerUnmarshallerHttpRequestInterceptorArrayHttpResponseInterceptorArrayClientInterceptorArray() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		assertTrue(test.createDefaultWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller, mockUnmarshaller,
				reqInterceptors, respInterceptors, intercpetors) instanceof WebServiceTemplate);
	}

	@Test
	public void testCreateSaajWebServiceTemplate() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		try {
			assertTrue(test.createSaajWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller, mockUnmarshaller,
					intercpetors) instanceof WebServiceTemplate);
		} catch (SOAPException e) {

		}
	}

	@Test
	public void testAddSslContext() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		HttpClientBuilder httpClient = HttpClients.custom();

		Resource mockKeystore = mock(KEYSTORE.getClass());

		// IOException
		try {
			Mockito.lenient().when(mockKeystore.getInputStream()).thenThrow(UnsupportedOperationException.class);
		} catch (IOException e1) {
			fail("Mocking should not throw exception");
		}
		try {
			test.addSslContext(httpClient, mockKeystore, KEYSTORE_PASS, TRUSTSTORE, TRUSTSTORE_PASS);
			fail("Should have thrown exception");
		} catch (Exception e) {
			assertTrue(e.getClass().isAssignableFrom(UnsupportedOperationException.class));
		}
	}

	@Test
	public void testCreateSslWebServiceTemplateStringIntIntMarshallerUnmarshaller() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		assertTrue(test.createSslWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller, mockUnmarshaller,
				KEYSTORE, KEYSTORE_PASS, TRUSTSTORE, TRUSTSTORE_PASS) instanceof WebServiceTemplate);
	}

	@Test
	public void testCreateSslWebServiceTemplateStringIntIntMarshallerUnmarshallerClientInterceptorArray() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		assertTrue(test.createSslWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller, mockUnmarshaller,
				intercpetors, KEYSTORE, KEYSTORE_PASS, TRUSTSTORE, TRUSTSTORE_PASS) instanceof WebServiceTemplate);
	}

	@Test
	public void
	testCreateSslWebServiceTemplateStringIntIntMarshallerUnmarshallerHttpRequestInterceptorArrayHttpResponseInterceptorArrayClientInterceptorArray() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		assertTrue(test.createSslWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller, mockUnmarshaller,
				reqInterceptors, respInterceptors, intercpetors, KEYSTORE, KEYSTORE_PASS, TRUSTSTORE,
				TRUSTSTORE_PASS) instanceof WebServiceTemplate);
	}

	@Test
	public void testCreateSaajSslWebServiceTemplate() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		try {
			assertTrue(test.createSaajSslWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller, mockUnmarshaller,
					intercpetors, KEYSTORE, KEYSTORE_PASS, TRUSTSTORE, TRUSTSTORE_PASS) instanceof WebServiceTemplate);
		} catch (SOAPException e) {

		}
	}

	@Test
	public void testCreateWebServiceTemplate() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		assertTrue(test.createWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller, mockUnmarshaller,
				reqInterceptors, respInterceptors, intercpetors, mockWebServiceMessageFactory, KEYSTORE, KEYSTORE_PASS, TRUSTSTORE,
				TRUSTSTORE_PASS) instanceof WebServiceTemplate);

		test = new BaseWsClientConfig();
		assertTrue(test.createWebServiceTemplate("http://dummyservice/endpoint", 30, 30, mockMarshaller, mockUnmarshaller,
				reqInterceptors, respInterceptors, intercpetors, mockWebServiceMessageFactory, null, null, null,
				null) instanceof WebServiceTemplate);
	}

	@Test
	public void testGetBeanNameAutoProxyCreator() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		String beanNames[] = { "TestBeanName" };
		String interceptorNames[] = { "TestInterceptorName" };
		assertTrue(test.getBeanNameAutoProxyCreator(beanNames, interceptorNames) instanceof BeanNameAutoProxyCreator);
	}

	@Test
	public void testGetMarshaller() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		Resource resources[] = { mockResource };
		try {
			test.getMarshaller("com.oracle.xmlns.internal.webservices.jaxws_databinding", resources, true);
		} catch (Exception e) {
			Assert.assertTrue(BipPartnerRuntimeException.class.isAssignableFrom(e.getClass()));
			Assert.assertTrue(e.getCause() instanceof IllegalArgumentException);
			Assert.assertNotNull(e.getMessage());
		}
	}

	// pkg-gov.va.bip.reference.partner.person.ws.transfer
	// resource-xsd/PersonService/PersonWebService.xsd
	@Test
	public void testGetPerformanceLogMethodInterceptor() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		assertTrue(test.getPerformanceLogMethodInterceptor(5) instanceof PerformanceLogMethodInterceptor);
	}

	@Test
	public void testGetVAServiceWss4jSecurityInterceptor() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		assertTrue(test.getVAServiceWss4jSecurityInterceptor("testuser", "test123", "EVSS",
				"STN_ID") instanceof VAServiceWss4jSecurityInterceptor);
	}

	@Test(expected = BipRuntimeException.class)
	public void testHandleExceptions() {
		BaseWsClientConfig test = new BaseWsClientConfig();
		ReflectionTestUtils.invokeMethod(test, "handleExceptions", new Exception());
	}

}
