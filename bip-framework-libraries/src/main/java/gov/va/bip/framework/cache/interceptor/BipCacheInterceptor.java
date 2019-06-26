package gov.va.bip.framework.cache.interceptor;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;

import gov.va.bip.framework.audit.AuditEventData;
import gov.va.bip.framework.audit.AuditEvents;
import gov.va.bip.framework.audit.BaseAsyncAudit;
import gov.va.bip.framework.audit.model.HttpResponseAuditData;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

/**
 * Audit cache GET operations.
 * <p>
 * This interceptor is equivalent to an Around aspect of the method that
 * has the Cache annotation(s) - e.g. @CachePut.
 * <p>
 * This interceptor does not distinguish cache operations, so all executions
 * of the application caching method will create audit records.
 * If this behavior is undesirable, it will be necessary to override enough
 * of the inherited code to have control of the {@link #doGet(Cache, Object)} method.
 *
 * @author aburkholder
 */
public class BipCacheInterceptor extends CacheInterceptor {
	private static final long serialVersionUID = -7142978196480878033L;

	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipCacheInterceptor.class);
	/** The advice logging name for this interceptor */
	private static final String ADVICE_NAME = "invokeBipCacheInterceptor";
	/** The activity name for this interceptor */
	private static final String ACTIVITY = "cacheInvoke";

	/** Get the object for general auditing. */
	@Autowired
	transient BaseAsyncAudit baseAsyncAudit;

	/**
	 * Instantiate an BipCacheInterceptor to audit cache GET operations.
	 */
	public BipCacheInterceptor() {
		LOGGER.debug("Instantiating " + BipCacheInterceptor.class.getName());
	}

	/**
	 * Perform audit logging after the method has been called.
	 * <p>
	 * This interceptor is equivalent to an Around aspect of the method that
	 * has the Cache annotation(s) - e.g. @CachePut.
	 * <p>
	 * This interceptor does not distinguish cache operations, so all executions
	 * of the application caching method will create audit records.
	 * If this behavior is undesirable, it will be necessary to override enough
	 * of the inherited code to have control of the {@link #doGet(Cache, Object)} method.
	 */
	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {

		Class<?> underAudit = invocation.getThis().getClass();
		AuditEventData auditEventData = new AuditEventData(AuditEvents.CACHED_SERVICE_RESPONSE, ACTIVITY, underAudit.getName());

		Object response = null;

		try {
			response = super.invoke(invocation);
			if (response == null) {
				// no response
				response = new Object();
			}

			if (LOGGER.isDebugEnabled()) {
				String prefix = this.getClass().getSimpleName() + ".invoke(..) :: ";
				LOGGER.debug(prefix + "Invocation class: " + invocation.getClass().toGenericString());
				LOGGER.debug(prefix + "Invoked from: " + invocation.getThis().getClass().getName());
				LOGGER.debug(prefix + "Invoking method: " + invocation.getMethod().toGenericString());
				LOGGER.debug(prefix + "  having annotations: " + Arrays.toString(invocation.getStaticPart().getAnnotations()));
				LOGGER.debug(prefix + "Returning: " + ReflectionToStringBuilder.toString(response, null, false, false, Object.class));
			}

			baseAsyncAudit.writeResponseAuditLog(response, new HttpResponseAuditData(), auditEventData, null, null);
			LOGGER.debug(ADVICE_NAME + " audit logging handed off to async.");

		} finally {
			LOGGER.debug(ADVICE_NAME + " finished.");
		}

		return response;
	}
}
