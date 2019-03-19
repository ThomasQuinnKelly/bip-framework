package gov.va.ocp.framework.rest.provider.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import gov.va.ocp.framework.aspect.PerformanceLoggingAspect;

@Aspect
@Order(-9999)
public class RestProviderTimerAspect extends BaseHttpProviderAspect {

	@Around("publicServiceResponseRestMethod()")
	public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		// thrown exceptions are handled in the PerformanceLoggingAspect
		return PerformanceLoggingAspect.aroundAdvice(joinPoint);
	}

}
