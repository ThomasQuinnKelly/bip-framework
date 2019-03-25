package gov.va.ocp.framework.service.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.event.Level;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
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
	public Object aroundAdvice(final ProceedingJoinPoint joinPoint) {

		DomainResponse domainResponse = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(this.getClass().getSimpleName() + " executing around method:" + joinPoint.toLongString());
			}

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
			if (methodParams != null) {
				List<ServiceMessage> messages = new ArrayList<>();

				for (final Object arg : methodParams) {
					validateRequest(arg, messages, method);
				}
				// add any validation error messages
				if (!messages.isEmpty()) {
					domainResponse = new DomainResponse();
					domainResponse.addMessages(messages);
				}
			}

			// if there were no errors, proceed with the actual method
			if ((domainResponse == null) || (domainResponse.getMessages() == null) || domainResponse.getMessages().isEmpty()) {
				domainResponse = (DomainResponse) joinPoint.proceed();

				// only call post-proceed() validation if there are no errors on the response
				if ((domainResponse != null) && !(domainResponse.hasErrors() || domainResponse.hasFatals())) {
					validateResponse(domainResponse, domainResponse.getMessages(), method, joinPoint.getArgs());
				}
			}

		} catch (final Throwable throwable) {
			if (OcpRuntimeException.class.isAssignableFrom(throwable.getClass())) {
				throw (OcpRuntimeException) throwable;
			} else {
				LOGGER.error(new OcpBanner("Aspect Error", Level.ERROR),
						this.getClass().getSimpleName() + " encountered " + throwable.getClass().getName()
						+ ": " + throwable.getMessage(),
						throwable);
				throw new OcpRuntimeException("", "", MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, throwable);
			}
		} finally {
			LOGGER.debug(this.getClass().getSimpleName() + " after method was called.");
		}

		return domainResponse;

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
			throw new OcpRuntimeException("", "No validator available for object of type " + object.getClass().getName(),
					MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// invoke the validator
		try {
			Validator<?> validator = (Validator<?>) validatorClass.newInstance();
			validator.setCallingMethod(callingMethod);
			validator.initValidate(object, messages);

		} catch (InstantiationException | IllegalAccessException e) {
			// Validator programming issue - throw exception
			String msg = "Could not find or instantiate class '" + validatorClass.getName()
			+ "'. Ensure that it has a no-arg constructor, and implements " + Validator.class.getName();
			LOGGER.error(msg, e);
			throw new OcpRuntimeException("", msg, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
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

		// invoke the validator
		try {
			if (validatorClass == null) {
				throw new OcpRuntimeException("", "No validator available for object of type " + object.getClass().getName(),
						MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			invokeValidate(object, messages, callingMethod, validatorClass, requestObjects);

		} catch (InstantiationException | IllegalAccessException e) {
			// Validator programming issue - throw exception
			String msg = "Could not find or instantiate class '" + validatorClass.getName()
			+ "'. Ensure that it has a no-arg constructor, and implements " + Validator.class.getName();
			LOGGER.error(msg, e);
			throw new OcpRuntimeException("", msg, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
	}

	private void invokeValidate(final DomainResponse object, final List<ServiceMessage> messages, final Method callingMethod,
			final Class<?> validatorClass, final Object... requestObjects) throws InstantiationException, IllegalAccessException {
		Validator<?> validator = (Validator<?>) validatorClass.newInstance();
		validator.setCallingMethod(callingMethod);
		validator.initValidate(object, messages, requestObjects);
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
			LOGGER.debug("Could not find validator class " + qualifiedValidatorName
					+ " - skipping validation for object " + ReflectionToStringBuilder.toString(object));
		}

		return validatorClass;
	}
}