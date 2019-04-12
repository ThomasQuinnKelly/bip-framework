package gov.va.bip.framework.rest.provider.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import gov.va.bip.framework.aspect.PerformanceLoggingAspect;

@Aspect
@Order(-9999)
public class RestProviderTimerAspect extends BaseHttpProviderPointcuts {

	@Around("publicServiceResponseRestMethod()")
	public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		// thrown exceptions are handled in the PerformanceLoggingAspect
		return PerformanceLoggingAspect.aroundAdvice(joinPoint);
	}

}
