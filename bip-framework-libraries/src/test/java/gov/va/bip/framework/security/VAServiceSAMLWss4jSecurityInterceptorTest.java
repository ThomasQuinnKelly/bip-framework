package gov.va.bip.framework.security;

import gov.va.bip.framework.config.BaseYamlConfig;
import gov.va.bip.framework.config.BipCommonSpringProfiles;
import gov.va.bip.framework.exception.BipRuntimeException;
import org.apache.ws.security.WSSecurityException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(inheritListeners = false, listeners = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@ActiveProfiles({ BipCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@ContextConfiguration(inheritLocations = false, classes = { BaseYamlConfig.class })
public class VAServiceSAMLWss4jSecurityInterceptorTest {

	private static final String SAML_FILE = "src/test/resources/encryption/EFolderService/SamlTokenEBN-UAT.xml";
	private static final String SAML_FILE_NOT_XML = "src/test/resources/testFiles/testFile1.txt";
	private static final String SAML_STRING = "<saml2:Assertion xmlns:saml2='urn:oasis:names:tc:SAML:2.0:assertion' xmlns:xs='http://www.w3.org/2001/XMLSchema' ID='37dc18fa-3d5e-420f-96b5-745b5bb823f7' IssueInstant='2012-12-19T19:13:12.851Z' Version='2.0'></saml2:Assertion>";
	private static final String NONEXISTENT_FILE = "someFileNameThatDoesNotExist.xml";

	private static final String SOAP_MESSAGE_FILE = "src/test/resources/testFiles/security/soapMessageMustUnderstand.xml";

	@Mock
	VAServiceSAMLWss4jSecurityInterceptor interceptor = new VAServiceSAMLWss4jSecurityInterceptor();

	@Before
	public void setup() {
		interceptor.setValidationActions("NoSecurity");
		interceptor.setValidateRequest(false);
		interceptor.setValidateResponse(false);
		interceptor.setSecurementUsername("selfsigned");
		interceptor.setSecurementPassword("password");
		interceptor.setSamlFile(SAML_FILE);
		try {
			interceptor.afterPropertiesSet();
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
	}

	@Test
	public void testGettersAndSetters() {
		final VAServiceSAMLWss4jSecurityInterceptor interceptor = new VAServiceSAMLWss4jSecurityInterceptor();
		interceptor.setSamlFile(SAML_FILE);
		Assert.assertEquals(SAML_FILE, interceptor.getSamlFile());
	}

	@Test
	public void testSecureMessage() {

		SoapMessage sm = null;
		try {
			sm = WSInterceptorTestUtil.createSoapMessage(SOAP_MESSAGE_FILE);
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}

		final MessageContext messageContextMock = mock(MessageContext.class);
		interceptor.secureMessage(sm, messageContextMock);
		Assert.assertNotNull(sm.getDocument());

		final VAServiceSAMLWss4jSecurityInterceptor spiedInterceptor = Mockito.spy(interceptor);
		try {
			doReturn(null).when(spiedInterceptor).getSAMLAssertionAsElement();
		} catch (final WSSecurityException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
		spiedInterceptor.secureMessage(sm, messageContextMock);
		Assert.assertNotNull(sm.getDocument());

		try {
			doThrow(new WSSecurityException("Testing")).when(spiedInterceptor).getSAMLAssertionAsElement();
		} catch (final WSSecurityException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
		try {
			spiedInterceptor.secureMessage(sm, messageContextMock);
		} catch (BipRuntimeException e) {
			assertTrue((e.getCause() instanceof WSSecurityException) && e.getCause().getMessage().equals("Testing"));
		}

		Assert.assertNotNull(sm.getDocument());
	}

	@SuppressWarnings("null")
	@Test(expected = NullPointerException.class)
	public void testSecureMessageWithNullSoapMessage() {
		SoapMessage sm = null;
		final MessageContext messageContextMock = mock(MessageContext.class);
		interceptor.secureMessage(sm, messageContextMock);
		Assert.assertNotNull(sm.getDocument());
	}

	@Test(expected = NullPointerException.class)
	public void testSecureMessageWithNullDoc() {
		SoapMessage sm = mock(SoapMessage.class);
		when(sm.getDocument()).thenReturn(null);
		final MessageContext messageContextMock = mock(MessageContext.class);
		interceptor.secureMessage(sm, messageContextMock);
		Assert.assertNotNull(sm.getDocument());
	}

	@Test
	public void testGetSAMLAssertionAsElement_CatchBlock() {
		VAServiceSAMLWss4jSecurityInterceptor mockInterceptor = spy(VAServiceSAMLWss4jSecurityInterceptor.class);
		when(mockInterceptor.getUtfCharacterEncoding()).thenReturn("Invalid Encoding");
		mockInterceptor.setValidationActions("NoSecurity");
		mockInterceptor.setValidateRequest(false);
		mockInterceptor.setValidateResponse(false);
		mockInterceptor.setSecurementUsername("selfsigned");
		mockInterceptor.setSecurementPassword("password");
		mockInterceptor.setSamlFile(SAML_FILE_NOT_XML);
		try {
			mockInterceptor.afterPropertiesSet();
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}

		try {
			mockInterceptor.getSAMLAssertionAsElement();
		} catch (final WSSecurityException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
	}

	@Test
	public void testGetSAMLAssertionAsElementWithValidFile() {
		interceptor.setSamlFile(SAML_FILE);
		try {
			interceptor.afterPropertiesSet();
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}

		try {
			interceptor.getSAMLAssertionAsElement();
		} catch (final WSSecurityException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
	}
	
	@Test
	public void testGetSAMLAssertionAsElementWithValidTextFile() {
		interceptor.setSamlFile(SAML_STRING);
		try {
			interceptor.afterPropertiesSet();
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}

		try {
			interceptor.getSAMLAssertionAsElement();
		} catch (final WSSecurityException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
	}

	@Test
	public void testGetSAMLAssertionAsElement() {
		interceptor.setSamlFile(NONEXISTENT_FILE);
		try {
			interceptor.afterPropertiesSet();
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}

		try {
			interceptor.getSAMLAssertionAsElement();
		} catch (final WSSecurityException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
	}
}
