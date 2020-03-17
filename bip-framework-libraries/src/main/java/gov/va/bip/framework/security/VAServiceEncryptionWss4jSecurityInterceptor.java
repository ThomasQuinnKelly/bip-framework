package gov.va.bip.framework.security;

import gov.va.bip.framework.exception.BipRuntimeException;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecEncrypt;
import org.apache.ws.security.message.WSSecHeader;
import org.springframework.http.HttpStatus;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;

/**
 * A Wss4j2 Security Interceptor to encrypt secure message header and body.
 * <p>
 * Instantiation of this class requires a {@link #retrieveCryptoProps()} method
 * that returns a {@link CryptoProperties} implementation, as declared in
 * {@link AbstractWss4jSecurityInterceptor#retrieveCryptoProps()}.
 * <p>
 * A complete example can be found in the spring beans of vetservices-partner-efolder EFolderWsClientConfig.java.
 * <p>
 * <b>NOTE:</b> VBMS uses the same cert for ssl AND signing AND key time stamp AND decryption.
 * If future implementations require separate certificates, this code - and possibly the {@link CryptoProperties}
 * interface and certainly its implementations - will need to be modified to provide the additional alias(es).
 * <p>
 * Calling code would typically provide the method in-line during construction, for example:
 *
 * <pre>
 * new VAServiceEncryptionWss4jSecurityInterceptor() {
 * &#64;Override
 * public CryptoProperties retrieveCryptoProps() {
 * return new VAServiceEncryptionWss4jSecurityInterceptor interceptor = cryptoProps.retrieveCryptoProperties();
 * }
 * };
 */
public abstract class VAServiceEncryptionWss4jSecurityInterceptor extends AbstractWss4jSecurityInterceptor {

	/** The Constant LOGGER. */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(VAServiceEncryptionWss4jSecurityInterceptor.class);

	/** The kind of object being encrypted */
	private static final String ENCRYPT_OBJ = "SOAP Message";

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor#secureMessage(org.springframework.ws.soap .SoapMessage,
	 * org.springframework.ws.context.MessageContext)
	 */
	@Override
	protected final void secureMessage(final SoapMessage soapMessage, final MessageContext messageContext) {

		super.secureMessage(soapMessage, messageContext);

		WSSConfig.init();
		try {

			CryptoProperties props = retrieveCryptoProps();

			LOGGER.debug("Encrypting outgoing message...");

			final WSSecEncrypt encrypt = new WSSecEncrypt();
			encrypt.setUserInfo(props.getCryptoEncryptionAlias());

			final Document doc = soapMessage.getDocument();
			final WSSecHeader secHeader = new WSSecHeader();
			secHeader.insertSecurityHeader(doc);
			encrypt.setDocument(doc);

			encrypt.build(doc, CryptoFactory.getInstance(props), secHeader);

			soapMessage.setDocument(doc);
		} catch (final WSSecurityException e) {
			MessageKeys key = MessageKeys.BIP_SECURITY_ENCRYPT_FAIL;
			LOGGER.error(key.getMessage(ENCRYPT_OBJ), e);
			throw new BipRuntimeException(key, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e, ENCRYPT_OBJ);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor#validateMessage(org.springframework.ws.soap.SoapMessage,
	 * org.springframework.ws.context.MessageContext)
	 */
	@Override
	protected final void validateMessage(final SoapMessage soapMessage, final MessageContext messageContext) {
		super.validateMessage(soapMessage, messageContext);
	}
}
