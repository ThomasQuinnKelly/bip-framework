package gov.va.bip.framework.messages;

import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * A message @PropertySource for Service Bip*Exception and *Message list.
 * <p>
 * This class derives its values from the framework-messages.properties file.
 * that is added to the spring context as an {@code @PropertySource}.
 * Primarily used by
 * <p>
 * <u>Usage and Maintenance</u><br/>
 * Any change in framework-messages.properties must be reflected in this class.<br/>
 * Any change in this class must be reflected in framework-messages.properties.
 *
 * @author aburkholder
 */
public enum MessageKeys implements MessageKey {

	//
	// Default messages defined for enumerations SHOULD NOT have params ... no {} brackets.
	//

	/** No key provided or specified; no args */
	NO_KEY("NO_KEY", "Unknown, no key provided."),
	/** Key for warning messages; {0} = warning message */
	WARN_KEY("WARN", ""),
	/** Key for propagating exceptions as BipExceptionExtender; {0} = exception message */
	PROPAGATE("PROPAGATE", ""),

	/** Problem with reflection; {0} = class simple name */
	BIP_DEV_ILLEGAL_INSTANTIATION("bip.framework.dev.illegal.instantiation",
			"Do not instantiate static classes or do not try to instantiate when an appropriate constructor does not exist."),
	/** Problem with reflection; {0} = class simple name */
	BIP_DEV_ILLEGAL_ACCESS("bip.framework.dev.illegal.access", "Could not access appropriate constructor."),
	/**
	 * Problem with reflection; {0} = class being instantiated; {1} = action being taken; {2} = type being acted against; {3}
	 * super-interface
	 */
	BIP_DEV_ILLEGAL_INVOCATION("bip.framework.dev.illegal.invocation", "Could not find or instantiate class."),

	/** Malformed JMX ObjectName; {0} is the name of the class being registered as a bean; {1} is the ObjectName being registered */
	BIP_JMX_CACHE_NAMING_MALFORMED("bip.jmx.cache.naming.malformed",
			"Could not register class on the MBeanServer because its ObjectName is malformed."),
	/** Some pre-processing issue; {0} is the class name or JMX ObjectName being registered / deregistered */
	BIP_JMX_REGISTRATION_PRE("bip.jmx.registration.pre", "Problem with pre-registration or pre-deregistration of JMX MBean."),
	/** Non-compliant JMX bean; {0} is the class name or JMX ObjectName */
	BIP_JMX_BEAN_NONCOMPLIANT("bip.jmx.bean.noncompliant", "Proposed class is not a JMX compliant MBean."),

	/** Last resort, unexpected exception; {0} = exception class simple name; {1} = exception message */
	BIP_GLOBAL_GENERAL_EXCEPTION("bip.framework.global.general.exception", "Unexpected exception."),
	/** Exception handler cast failed; {0} = class name */
	BIP_EXCEPTION_HANDLER_ERROR_VALUES("bip.framework.exception.handler.error.values.",
			"Could not instantiate BipRuntimeException."),
	/** Exception handler cast failed; {0} = class name */
	BIP_EXCEPTION_HANDLER_ERROR_CAST("bip.framework.exception.handler.error.cast",
			"Could not cast throwable to BipRuntimeException."),
	/** MethodArgumentNotValidException; {0} = "field" or "object"; {1} = codes; {2} = default message */
	BIP_GLOBAL_VALIDATOR_METHOD_ARGUMENT_NOT_VALID("bip.framework.global.validator.method.argument.not.valid", "Argument not valid."),
	/** HttpClientErrorException; {0} = http status code; {1} = exception message */
	BIP_GLOBAL_HTTP_CLIENT_ERROR("bip.framework.global.http.client.error", "Client Error."),
	/** MethodArgumentTypeMismatchException; {0} = argument name; {1} = expected class name */
	BIP_GLOBAL_REST_API_TYPE_MISMATCH("bip.framework.global.rest.api.type.mismatch", "API argument type could not be resolved."),
	/** ConstraintViolationException; {0} = bean class name; {1} = property name; {2} = violation message */
	BIP_GLBOAL_VALIDATOR_CONSTRAINT_VIOLATION("bip.framework.global.validator.constraint.violation",
			"Validation constraint was violated."),

	/** JAXB Marshaller configuration failed; no args */
	BIP_REST_CONFIG_JAXB_MARSHALLER_FAIL("bip.framework.rest.config.jaxb.marshaller.failed", "Error configuring JAXB marshaller."),
	/** WebserviceTemplate configuration failed; no args */
	BIP_REST_CONFIG_WEBSERVICE_TEMPLATE_FAIL("bip.framework.rest.config.webservice.template.failed",
			"Unexpected exception thrown by WebServiceTemplate."),
	/** Propogate message from other service; {0} = message key; {1} = message text */
	BIP_FEIGN_MESSAGE_RECEIVED("bip.framework.feign.message.received", "External service returned error message."),

	/** JWT token is invalid; no args */
	BIP_SECURITY_TOKEN_INVALID("bip.framework.security.token.invalid", "Invalid Token."),
	/** JWT token cannot be blank; no args */
	BIP_SECURITY_TOKEN_BLANK("bip.framework.security.token.blank", "No JWT Token in Header."),
	/**
	 * JWT token cannot be blank; #{0} = the problem; {1} = the token; {2} = the simple class name of the exception {3} = message from
	 * the exception
	 */
	BIP_SECURITY_TOKEN_BROKEN("bip.framework.security.token.broken", "JWT Token is not valid."),
	/** Correlation IDs passed in the JWT token cannot be blank; no args */
	BIP_SECURITY_TRAITS_CORRELATIONID_BLANK("bip.framework.security.traits.correlationid.blank",
			"Cannot process blank correlation id."),
	/** Correlation IDs passed in the JWT token; {0} = ELEMENT_SS_COUNT, {1} = ELEMENT_MAX_COUNT */
	BIP_SECURITY_TRAITS_CORRELATIONID_INVALID("bip.framework.security.traits.correlationid.invalid",
			"Invalid number of elements in correlation id."),
	/** IdType specified does not exist; {0} = IdTypes.[value] */
	BIP_SECURITY_TRAITS_IDTYPE_INVALID("bip.framework.security.traits.idtype.invalid",
			"Specified IdType does not exist."),
	/** Issuer specified does not exist; {0} = Issuers.[value] */
	BIP_SECURITY_TRAITS_ISSUER_INVALID("bip.framework.security.traits.issuer.invalid",
			"Specified Issuer does not exist."),
	/** Source specified does not exist; {0} = Sources.[value] */
	BIP_SECURITY_TRAITS_SOURCE_INVALID("bip.framework.security.traits.source.invalid",
			"Specified Source does not exist."),
	/** UserStatus specified does not exist; {0} = UserStatus.[value] */
	BIP_SECURITY_TRAITS_USERSTATUS_INVALID("bip.framework.security.traits.userstatus.invalid",
			"Specified UserStatus does not exist."),
	/** Encryption failed for some reason; {0} = kind of object that was being encrypted */
	BIP_SECURITY_ENCRYPT_FAIL("bip.framework.security.encrypt.failed", "Encryption failed."),
	/** Signing failed for some reason; {0} = kind of object that was being signed */
	BIP_SECURITY_SIGN_FAIL("bip.framework.security.sign.failed", "Could not sign."),
	/** Encryption failed for some reason; {0} = action being taken on the attribute; {1} attribute name */
	BIP_SECURITY_ATTRIBUTE_FAIL("bip.framework.security.attribute.failed", "Could not modify attribute."),
	/** SAML insertion failed; no args */
	BIP_SECURITY_SAML_INSERT_FAIL("bip.framework.security.saml.insert.failed", "SAML insertion failed."),
	/** SSL initialization failed {0} = exception simple class name; {1} = exception message */
	BIP_SECURITY_SSL_CONTEXT_FAIL("bip.framework.security.ssl.context.failed", "Could not establish SSL context."),

	/** Sanitizing filename failed; {0} = operation */
	BIP_SECURITY_SANITIZE_FAIL("bip.framework.security.sanitize.failed", "Unexpected error: {0}."),

	/** Auditing error during cache operations; {0} = advice name, {1} = operation attempted */
	BIP_AUDIT_CACHE_ERROR_UNEXPECTED("bip.framework.audit.cache.error.unexpected",
			"An unexpected error occurred while auditing cache retrieval."),
	/** Auditing error produced by the aspect/interceptor itself; {0} = advice name, {1} = operation attempted */
	BIP_AUDIT_ASPECT_ERROR_UNEXPECTED("bip.framework.audit.aspect.error.unexpected",
			"An unexpected error occurred while auditing from aspect/interceptor."),
	/** Auditing appears to be broken; {0} = advice name, {1} = originating throwable that called writeAuditError */
	BIP_AUDIT_ASPECT_ERROR_CANNOT_AUDIT("bip.framework.audit.aspect.error.cannot.audit",
			"Cannot write audit error for throwables."),

	/** Validator initialization; no args */
	BIP_VALIDATOR_INITIALIZE_ERROR_UNEXPECTED("bip.framework.validator.initialize.error.unexpected",
			"Could not initialize standard validator."),
	BIP_VALIDATOR_ASSERTION("bip.framework.validator.assertion", "Assertion failed."),
	/** Object cannot be null; {0} the object that cannot be null */
	BIP_VALIDATOR_NOT_NULL("bip.framework.validator.not.null", "Object cannot be null."),
	/** {0} = validated object class name; {1} = expected class name */
	BIP_VALIDATOR_TYPE_MISMATCH("bip.framework.validator.type.mismatch", "Validated object is not of excpected type."),

	/** Simulator could not find mock response file; {0} = XML file name; {1} = key used to construct file name */
	BIP_REMOTE_MOCK_NOT_FOUND("bip.framework.remote.mock.not.found",
			"Could not read mock XML file. Please make sure the correct response file exists in the main/resources directory."),
	/**
	 * RemoteServiceCallMock is not set up to process a type; {0} = the RemoteServiceCallMock class; {1} = the class used in the
	 * request
	 */
	BIP_REMOTE_MOCK_UNKNOWN("bip.framework.remote.mock.unknown.type",
			"RemoteServiceCallMock getKeyForMockResponse(..) does not have a file naming block for request type.")

	;

	/** The filename "name" part of the properties file to get from the classpath */
	private static final String PROPERTIES_FILE = "framework-messages";
	/** The spring message source */
	private static ReloadableResourceBundleMessageSource messageSource;
	/* Populate the message source from the properties file */
	static {
		messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:" + PROPERTIES_FILE);
		messageSource.setDefaultEncoding("UTF-8");
	}

	/** The key - must be identical to the key in framework-messages.properties */
	private String key;
	/** A default message, in case the key is not found in framework-messages.properties */
	private String defaultMessage;

	/**
	 * Construct keys with their property file counterpart key and a default message.
	 *
	 * @param key - the key as declared in the properties file
	 * @param defaultMessage - in case the key cannot be found
	 */
	private MessageKeys(final String key, final String defaultMessage) {
		this.key = key;
		this.defaultMessage = defaultMessage;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.bip.framework.messages.MessageKey#getKey()
	 */
	@Override
	public String getKey() {
		return this.key;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.bip.framework.messages.MessageKey#getMessage(java.lang.String[])
	 */
	@Override
	public String getMessage(final String... params) {
		return messageSource.getMessage(this.key, params, this.defaultMessage, Locale.getDefault());
	}
}
