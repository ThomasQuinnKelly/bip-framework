package gov.va.ocp.framework.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
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

import gov.va.ocp.framework.constants.AnnotationConstants;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * A Wss4j2 Security Interceptor to add a SAML assertion to the secure message header.
 */
public class VAServiceSAMLWss4jSecurityInterceptor extends Wss4jSecurityInterceptor {

	/** The Constant LOGGER. */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(VAServiceSAMLWss4jSecurityInterceptor.class);

	/** The Constant ERROR_SAML_ASSERTION. */
	private static final String ERROR_SAML_ASSERTION = "Error while attempting to convert SAML assertion string to element.";

	/** The saml file. */
	private String samlFile;

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
			final Document doc = soapMessage.getDocument();
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

			soapMessage.setDocument(doc);
			LOGGER.info("SOAP message: "
					+ ReflectionToStringBuilder.toString(soapMessage == null ? "null" : soapMessage, ToStringStyle.SHORT_PREFIX_STYLE, // NOSONAR
							true, true, null));

		} catch (final WSSecurityException e) {
			String msg = "Error while attempting to insert SAML Assertion into message.";
			LOGGER.error(msg, e);
			throw new OcpRuntimeException("", msg, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
	}

	/**
	 * Gets the sAML assertion as element.
	 *
	 * @return the sAML assertion as element
	 */
	Element getSAMLAssertionAsElement() throws WSSecurityException { // NOSONAR throws to assist unit testing and coverage
		Element retVal = null;
		String clientAssertion = null;

		try (InputStream input = getSamlFile().endsWith(".xml") ? new FileSystemResource(getSamlFile()).getInputStream()
				: new ByteArrayInputStream(getSamlFile().getBytes())) {
			clientAssertion = IOUtils.toString(input, "UTF-8");
		} catch (final Exception e) {
			LOGGER.error(OcpBanner.newBanner(AnnotationConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
					"Unable to read SAML assertion from file." + getSamlFile(), e);
			return retVal;
		}

		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			final DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			final InputSource inStream = new InputSource();

			LOGGER.info("Client SAML assertion XML String : " + clientAssertion);

			inStream.setCharacterStream(new StringReader(clientAssertion));

			final Document doc = builder.parse(inStream);
			retVal = doc.getDocumentElement();

		} catch (final ParserConfigurationException | SAXException | IOException e) {
			LOGGER.error(OcpBanner.newBanner(AnnotationConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
					ERROR_SAML_ASSERTION, e);
		}

		return retVal;
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
