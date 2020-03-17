package gov.va.bip.framework.security;

import gov.va.bip.framework.constants.BipConstants;
import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.message.WSSecHeader;
import org.slf4j.event.Level;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * A Wss4j2 Security Interceptor to add a SAML assertion to the secure message header.
 */
public class VAServiceSAMLWss4jSecurityInterceptor extends Wss4jSecurityInterceptor {

	/** The Constant LOGGER. */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(VAServiceSAMLWss4jSecurityInterceptor.class);

	/** The Constant ERROR_SAML_ASSERTION. */
	private static final String ERROR_SAML_ASSERTION = "Error while attempting to convert SAML assertion string to element.";

	/** The saml file. */
	private String samlFile = StringUtils.EMPTY;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor#secureMessage(org.springframework.ws.soap .SoapMessage,
	 * org.springframework.ws.context.MessageContext)
	 */
	@Override
	protected void secureMessage(final SoapMessage soapMessage, final MessageContext messageContext) {

		super.secureMessage(soapMessage, messageContext);

		try {
			Document doc = null;

			if (soapMessage != null && soapMessage.getDocument() != null) {
				doc = soapMessage.getDocument();
			}

			final WSSecHeader secHeader = new WSSecHeader();
			LOGGER.info("doc before security header: "
					+ ReflectionToStringBuilder.toString(doc == null ? "null" : doc, ToStringStyle.SHORT_PREFIX_STYLE, true, true,
							null));
			secHeader.insertSecurityHeader(doc);
			LOGGER.info("doc after security header: "
					+ ReflectionToStringBuilder.toString(doc == null ? "null" : doc, ToStringStyle.SHORT_PREFIX_STYLE, true, true,
							null));

			final Element xml = getSAMLAssertionAsElement();
			LOGGER.info(
					"SAML Assertion: " + ReflectionToStringBuilder.toString(xml == null ? "null" : xml,
							ToStringStyle.SHORT_PREFIX_STYLE, false, true, null));

			if (xml != null) {
				final Document headerDoc = secHeader.getSecurityHeader().getOwnerDocument();
				final Node importedNode = headerDoc.importNode(xml, true);
				secHeader.getSecurityHeader().appendChild(importedNode);
			} else {
				LOGGER.error("Could not find an existing SAML assertion to use in web service request.");
			}

			if (soapMessage != null) {
				soapMessage.setDocument(doc);
			}

			LOGGER.info("SOAP message: "
					+ ReflectionToStringBuilder.toString(soapMessage == null ? "null" : soapMessage, ToStringStyle.SHORT_PREFIX_STYLE, // NOSONAR
							true, true, null));

		} catch (final WSSecurityException e) {
			MessageKeys key = MessageKeys.BIP_SECURITY_SAML_INSERT_FAIL;
			LOGGER.error(key.getMessage(), e);
			throw new BipRuntimeException(key, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
	}

	/**
	 * Gets the SAML assertion as element.
	 *
	 * @return the sAML assertion as element
	 */
	Element getSAMLAssertionAsElement() throws WSSecurityException { // NOSONAR throws to assist unit testing and coverage
		Element retVal = null;
		String clientAssertion = null;

		if (getSamlFile().endsWith(".xml")) {
			try (InputStream input = new FileSystemResource(getSamlFile()).getInputStream()) {
				clientAssertion = IOUtils.toString(input, getUtfCharacterEncoding());
			} catch (final Exception e) {
				LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
						"Unable to read SAML assertion from XML file." + getSamlFile(), e);
				return retVal;
			}
		} else {
			try (InputStream input = new ByteArrayInputStream(getSamlFile().getBytes())) {
				clientAssertion = IOUtils.toString(input, getUtfCharacterEncoding());
			} catch (final Exception e) {
				LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
						"Unable to read SAML assertion for a string value." + getSamlFile(), e);
				return retVal;
			}
		}
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false); 
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

			factory.setNamespaceAware(true);
			factory.setValidating(true);
			final DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			final InputSource inStream = new InputSource();

			LOGGER.info("Client SAML assertion XML String : " + clientAssertion);

			inStream.setCharacterStream(new StringReader(clientAssertion));

			final Document doc = builder.parse(inStream);
			retVal = doc.getDocumentElement();

		} catch (final ParserConfigurationException | SAXException | IOException e) {
			LOGGER.error(BipBanner.newBanner(BipConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
					ERROR_SAML_ASSERTION, e);
		}

		return retVal;
	}

	/**
	 * Gets the UTF character encoding.
	 * 
	 * preserve the visibility to package for negative unit test coverage
	 *
	 * @return the UTF character encoding
	 */
	String getUtfCharacterEncoding() {
		return StandardCharsets.UTF_8.name();
	}

	/**
	 * Gets the saml file.
	 *
	 * @return the saml file
	 */
	public final String getSamlFile() {
		return samlFile;
	}

	/**
	 * Sets the saml file.
	 *
	 * @param samlFile the new saml file
	 */
	public final void setSamlFile(final String samlFile) {
		this.samlFile = samlFile;
	}

}
