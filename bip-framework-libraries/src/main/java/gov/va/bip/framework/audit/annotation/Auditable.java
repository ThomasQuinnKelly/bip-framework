package gov.va.bip.framework.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import gov.va.bip.framework.audit.AuditEvents;

/**
 * Auditable annotation that asynchronously logs audit data.
 * Can be applied to methods or classes.
 * <p>
 * This annotation is intended for use in the service layers,
 * and should never be applied to a provider class or method.
 * <p>
 * Required attributes:
 * <ul>
 * <li>event - an {@link AuditEvents} audit event enumeration
 * <li>activity - specific String description of the event
 * </ul>
 * Optional attributes:
 * <ul>
 * <li>auditClass - name of the java Class under audit
 * </ul>
 *
 * Created by vgadda on 8/17/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Auditable {
	/** An {@link AuditEvents} audit event enumeration */
	AuditEvents event();

	/** Specific String description of the event */
	String activity();

	/** Name of the java Class under audit */
	String auditClass() default "";
}
