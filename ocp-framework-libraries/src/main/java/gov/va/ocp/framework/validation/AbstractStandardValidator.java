package gov.va.ocp.framework.validation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.exception.interceptor.ExceptionHandlingUtils;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.messages.ServiceMessage;

/**
 * An abstract implementation of the {@link Validator} interface.
 * <p>
 * Provides standardized pre-validation capabilities, and stores objects for use in the validator implementation.
 * See the {@link #initValidate(Object, List, Object...)} method for more information.
 *
 * @see Validator
 *
 * @author aburkholder
 *
 * @param <T> type-cast the object being validated
 */
public abstract class AbstractStandardValidator<T> implements Validator<T> {

	/** Class logger */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(AbstractStandardValidator.class);

	/** The class of the object that will be validated */
	private Class<T> toValidateClass;

	/** The method that caused the validator to be executed */
	private Method callingMethod;

	/** Full class.method name of the calling method */
	private String callingMethodName;

	/** Supplemental objects for processing the validation */
	private Object[] supplemental;

	public AbstractStandardValidator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * As a convenience to the developer, performs standardized pre-validation steps before calling the implementations
	 * {@link #validate(Object, List)} method.<br/>
	 * This method:
	 * <ul>
	 * <li>Can be strongly typed, per the class-level Type Parameter.
	 * <li>Does Exception handling for the entire validation process.
	 * <li>Stashes any supplemental objects for retrieval by {@link #getSupplemental()} and {@link #getSupplemental(Class)}.<br/>
	 * Examples of supplemental objects: while validating a response object, the request object is added as a supplemental in case it
	 * is needed.
	 * <li>Stashes the calling {@link Method} (if provided) for retrieval by {@link #getCallingMethod()} and
	 * {@link #getCallingMethodName()}.
	 * <li>Null checks the {@code toValidate} parameter. If the null check fails, returns with message ({@link #validate(Object, List)}
	 * method is never called).
	 * <li>Verifies that the class of the toValidate parameter is correct. If it fails, returns with message
	 * ({@link #validate(Object, List)} method is never called).
	 * <li>Null checks the messages parameter initializes it if necessary.
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initValidate(Object toValidate, List<ServiceMessage> messages, Object... supplemental) {
		try {
			this.supplemental = supplemental;

			if (messages == null) {
				messages = new ArrayList<>();
			}

			this.callingMethodName = callingMethod == null ? ""
					: callingMethod.getDeclaringClass().getSimpleName()
							+ "." + callingMethod.getName() + ": ";

			LOGGER.debug("Validating " + (toValidate == null ? "null" : toValidate.getClass().getSimpleName())
					+ " for " + callingMethodName);

			// request-level null check
			if (toValidate == null) {
				LOGGER.debug("Request is null");
				messages.add(new ServiceMessage(MessageSeverity.ERROR, "", callingMethodName + " Request cannot be null.",
						HttpStatus.BAD_REQUEST));
				return;
			}

			this.toValidateClass = (Class<T>) toValidate.getClass();
			// check class is correct
			if (!getValidatedType().isAssignableFrom(this.toValidateClass)) {
				String msg = callingMethodName + "Validated object '" + toValidate.getClass().getName()
						+ "' is not of type '" + getValidatedType().getName() + "'";
				LOGGER.debug(msg);
				messages.add(new ServiceMessage(MessageSeverity.ERROR, "", msg, HttpStatus.BAD_REQUEST));
				return;
			}

			// unchecked type-cast (but pre-verified above) to invoke implementation-specific validation
			validate((T) toValidate, messages);

		} catch (Throwable t) { // NOSONAR intentionally broad catch
			final OcpRuntimeException runtime = ExceptionHandlingUtils.resolveRuntimeException(t);
			if (runtime != null) {
				throw runtime;
			} else { 
				throw t;
			}
		}
	}

	/**
	 * Call the validate method on the validator for model object T.
	 * <p>
	 * This implementation pre-validates the following conditions of the {@code toValidate} parameter for you:
	 * <ul>
	 * <li>Stash any supplemental objects for retrieval by {@link #getSupplemental()} and {@link #getSupplemental(Class)}.<br/>
	 * Examples of supplemental objects: while validating a response object, the request object is added as a supplemental in case it
	 * is
	 * needed.
	 * <li>Stash the calling {@link Method} (if provided) for retrieval by {@link #getCallingMethod()} and
	 * {@link #getCallingMethodName()}
	 * <li>Null check the {@code toValidate} parameter. If the null check fails, returns with message ({@link #validate(Object, List)}
	 * method is never called)
	 * <li>Class of the toValidate parameter verified to be correct. If it fails, returns with message ({@link #validate(Object, List)}
	 * method is never called)
	 * <li>messages parameter null checked and list initialized if necessary
	 * </ul>
	 *
	 * @see Validator
	 */
	@Override
	public abstract void validate(T toValidate, List<ServiceMessage> messages);

	@Override
	public Class<T> getValidatedType() {
		return toValidateClass;
	}

	@Override
	public void setCallingMethod(Method callingMethod) {
		this.callingMethod = callingMethod;
	}

	@Override
	public Method getCallingMethod() {
		return this.callingMethod;
	}

	/**
	 * The calling method name, derived from {@link #getCallingMethod()}.
	 *
	 * @return the callingMethodName
	 */
	protected String getCallingMethodName() {
		return callingMethodName;
	}

	protected boolean hasSupplemental() {
		return this.supplemental != null && this.supplemental.length > 0;
	}

	protected boolean hasSupplemental(Class<?> clazz) {
		if (clazz != null && hasSupplemental()) {
			for (Object obj : supplemental) {
				if (clazz.equals(obj.getClass())) {
					return true;
				}
			}
		}
		return false;
	}

	protected Object[] getSupplemental() {
		return this.supplemental;
	}

	protected Object getSupplemental(Class<?> clazz) {
		if (hasSupplemental(clazz)) {
			for (Object obj : supplemental) {
				if (clazz.equals(obj.getClass())) {
					return obj;
				}
			}
		}
		return null;
	}
}
