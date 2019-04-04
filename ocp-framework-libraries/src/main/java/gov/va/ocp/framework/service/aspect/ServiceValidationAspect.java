package gov.va.ocp.framework.service.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageKeys;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.messages.ServiceMessage;
import gov.va.ocp.framework.service.DomainResponse;
import gov.va.ocp.framework.validation.Validator;

/**
 * This aspect invokes business validations on eligible service API methods.
 *
 * Eligible service operations are any those which ...
 * <ol>
 * <li>have public scope
 * <li>have a spring @Service annotation
 * <li>have a companion validator named with the form <tt><i>ClassName</i>Validator</tt> that is in the "validators" package below
 * where the model object is found,
 * e.g. {@code gov.va.ocp.reference.api.model.v1.validators.PersonInfoValidator.java}.
 * </ol>
 * <p>
 * Validators called by this aspect <b>should</b> extend {@link gov.va.ocp.framework.validation.AbstractStandardValidator} or
 * similar implementation.
 *
 * @see gov.va.ocp.framework.validation.Validator
 * @see gov.va.ocp.framework.validation.AbstractStandardValidator
 *
 * @author aburkholder
 */
@Aspect
@Order(-9998)
public class ServiceValidationAspect extends BaseServiceAspect {

	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(ServiceValidationAspect.class);

	/** Text added to end of class name to determine its validator name */
	private static final String POSTFIX = "Validator";

	/**
	 * Around advice for{@link BaseServiceAspect#serviceImpl()} pointcut.
	 * <p>
	 * This method will execute validations on any parameter objects in the method signature.<br/>
	 * Any failed validations is added to the method's response object, and is audit logged.
	 * <p>
	 * Validators called by this aspect <b>should</b> extend {@link gov.va.ocp.framework.validation.AbstractStandardValidator} or
	 * similar implementation.
	 *
	 * @param joinPoint
	 * @return Object
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	@Around("publicStandardServiceMethod() && serviceImpl()")
	public Object aroundAdvice(final ProceedingJoinPoint joinPoint) throws Throwable {

		LOGGER.debug(this.getClass().getSimpleName() + " executing around method:" + joinPoint.toLongString());
		DomainResponse domainResponse = null;

		try {
			LOGGER.debug("Validating service interface request inputs.");

			// get the request and the calling method from the JoinPoint
			List<Object> methodParams = Arrays.asList(joinPoint.getArgs());
			Method method = null;
			if (joinPoint.getArgs().length > 0) {
				Class<?>[] methodParamTypes = new Class<?>[methodParams.size()];
				for (int i = 0; i < methodParams.size(); i++) {
					Object param = methodParams.get(i);
					methodParamTypes[i] = param == null ? null : param.getClass();
				}
				method = joinPoint.getSignature().getDeclaringType().getDeclaredMethod(joinPoint.getSignature().getName(),
						methodParamTypes);
			}

			// attempt to validate all inputs to the method
			validateInputsToTheMethod(domainResponse, methodParams, method);

			// if there were no errors from validation, proceed with the actual method
			if (!didValidationPass(domainResponse)) {
				LOGGER.debug("Service interface request validation failed. >>> Skipping execution of "
						+ joinPoint.getSignature().toShortString() + " and returning immediately.");
			} else {
				LOGGER.debug("Service interface request validation succeeded. Executing " + joinPoint.getSignature().toShortString());

				domainResponse = (DomainResponse) joinPoint.proceed();

				// only call post-proceed() validation if there are no errors on the response
				if ((domainResponse != null) && !(domainResponse.hasErrors() || domainResponse.hasFatals())) {
					LOGGER.debug("Validating service interface response outputs.");
					validateResponse(domainResponse, domainResponse.getMessages(), method, joinPoint.getArgs());
				}
			}
		} finally {
			LOGGER.debug(this.getClass().getSimpleName() + " after method was called.");
		}

		return domainResponse;

	}

	/**
	 * Returns {@code true} if DomainResponse is not {@code null} and its messages list is {@code null} or empty.
	 */
	private boolean didValidationPass(final DomainResponse domainResponse) {
		return domainResponse == null || (domainResponse.getMessages() == null || domainResponse.getMessages().isEmpty());
	}

	/**
	 * Validates all input args to a method.
	 *
	 * @param methodParams - the method args
	 * @param method - the method being executed
	 */
	private void validateInputsToTheMethod(DomainResponse response, final List<Object> methodParams, final Method method) {
		if (methodParams != null) {
			List<ServiceMessage> messages = new ArrayList<>();

			for (final Object arg : methodParams) {
				validateRequest(arg, messages, method);
			}
			// add any validation error messages
			if (!messages.isEmpty()) {
				if (response == null) {
					response = new DomainResponse();
				}
				response.addMessages(messages);
			}
		}
	}

	/**
	 * Use ONLY for exceptions raised due to:
	 * <ul>
	 * <li>issues with acquiring the validator class for the originating service impl
	 * <li>issues instantiating the validator class
	 * </ul>
	 *
	 * @param validatorClass
	 * @param e
	 * @param object
	 * @throws OcpRuntimeException
	 */
	private void handleValidatorInstantiationExceptions(final Class<?> validatorClass, final Exception e, final Object object) {
		// Validator programming issue - throw exception
		MessageKeys key = MessageKeys.OCP_DEV_ILLEGAL_INVOCATION;
		Object[] params = new Object[] { (validatorClass != null ? validatorClass.getName() : "null"), "validate",
				object.getClass().getName(), Validator.class.getName() };
		LOGGER.error(key.getMessage(params), e);
		throw new OcpRuntimeException(key, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e, params);
	}

	/**
	 * Locate the {@link Validator} for the request object, and if it exists,
	 * invoke the {@link Validator#getValidatedType()} method.
	 * <p>
	 * Validator implementations <b>must</b> exist in a validators package
	 * under the package in which {@code object} exists.
	 *
	 * @see gov.va.ocp.framework.validation.Validator
	 * @see gov.va.ocp.framework.validation.AbstractStandardValidator
	 *
	 * @param object the object to validate
	 * @param messages list on which to return validation messages
	 * @param callingMethod optional; the method that caused this validator to be called
	 */
	private void validateRequest(final Object object, final List<ServiceMessage> messages, final Method callingMethod) {

		Class<?> validatorClass = this.resolveValidatorClass(object);

		if (validatorClass == null) {
			handleValidatorInstantiationExceptions(validatorClass,
					new NullPointerException("No validator available for object of type " + object.getClass().getName()), object);
		}

		// invoke the validator - no supplemental objects
		try {
			invokeValidator(object, messages, callingMethod, validatorClass);

		} catch (InstantiationException | IllegalAccessException | NullPointerException e) {
			handleValidatorInstantiationExceptions(validatorClass, e, object);
		}
	}

	private void invokeValidator(final Object object, final List<ServiceMessage> messages, final Method callingMethod,
			final Class<?> validatorClass, Object... supplemental) throws InstantiationException, IllegalAccessException {
		Validator<?> validator = (Validator<?>) validatorClass.newInstance();
		validator.setCallingMethod(callingMethod);
		validator.initValidate(object, messages, supplemental);
	}

	/**
	 * Locate the {@link Validator} for the object, and if it exists,
	 * invoke the {@link Validator#getValidatedType()} method.
	 * <p>
	 * Validator implementations <b>must</b> exist in a validators package
	 * under the package in which {@code object} exists.
	 *
	 * @see gov.va.ocp.framework.validation.Validator
	 * @see gov.va.ocp.framework.validation.AbstractStandardValidator
	 *
	 * @param object
	 * @param messages
	 * @param callingMethod
	 * @param requestObjects
	 */
	private void validateResponse(final DomainResponse object, final List<ServiceMessage> messages, final Method callingMethod,
			final Object... requestObjects) {

		Class<?> validatorClass = this.resolveValidatorClass(object);

		if (validatorClass == null) {
			handleValidatorInstantiationExceptions(validatorClass,
					new NullPointerException("No validator available for object of type " + object.getClass().getName()), object);
		}

		// invoke the validator, sned request objects as well
		try {
			invokeValidator(object, messages, callingMethod, validatorClass, requestObjects);

		} catch (InstantiationException | IllegalAccessException | NullPointerException e) {
			handleValidatorInstantiationExceptions(validatorClass, e, object);
		}
	}

	/**
	 * Determine the Validator class for the model object that is to be validated.
	 * <p>
	 * The pattern for Validator classes is:<br/>
	 * <tt><i>model.objects.class.package</i>.validators.<i>ModelObjectClassSimpleName</i>Validator</tt>
	 *
	 * @param object
	 * @return
	 */
	private Class<?> resolveValidatorClass(final Object object) {
		// Deduce the validator class name based on the pattern
		String qualifiedValidatorName = object.getClass().getPackage() + ".validators." + object.getClass().getSimpleName() + POSTFIX;
		qualifiedValidatorName = qualifiedValidatorName.replaceAll("package\\s+", "");

		// find out if a validator exists for object
		Class<?> validatorClass = null;
		try {
			validatorClass = Class.forName(qualifiedValidatorName);
		} catch (ClassNotFoundException e) {
			// no validator, return without error
			LOGGER.error("Could not find validator class " + qualifiedValidatorName
					+ " - skipping validation for object " + ReflectionToStringBuilder.toString(object), e);
		}

		return validatorClass;
	}
}
