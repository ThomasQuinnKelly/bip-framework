package gov.va.ocp.framework.rest.provider.aspect;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.event.Level;

import gov.va.ocp.framework.AbstractBaseLogTester;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.rest.provider.aspect.RestProviderTimerAspect;

@RunWith(MockitoJUnitRunner.class)
public class RestProviderTimerAspectTest extends AbstractBaseLogTester {

	/** Underlying implementation of OcpLogger */
	private OcpLogger AspectLoggingLOG = super.getLogger(RestProviderTimerAspectTest.class);
	/** Underlying implementation of OcpLogger */
	private OcpLogger AspectLoggingTestLOG = super.getLogger(RestProviderTimerAspectTest.class);

	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;

	@Mock
	private MethodSignature signature;

	@Mock
	private JoinPoint.StaticPart staticPart;

	@Override
	@Before
	public void setup() throws Throwable {
		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");
		when(proceedingJoinPoint.getStaticPart()).thenReturn(staticPart);
		when(staticPart.getSignature()).thenReturn(signature);
		when(signature.getMethod()).thenReturn(myMethod());
	}

	@Override
	@After
	public void tearDown() {
		AspectLoggingLOG.setLevel(Level.DEBUG);
		AspectLoggingTestLOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testAroundAdviceDebugOn() throws Throwable {
		super.getAppender().clear();

		RestProviderTimerAspect restProviderTimerAspect = new RestProviderTimerAspect();
		restProviderTimerAspect.aroundAdvice(proceedingJoinPoint);

		assertEquals("PerformanceLoggingAspect executing around method:ProceedingJoinPointLongString",
				super.getAppender().get(0).getMessage());
		assertEquals("enter [RestProviderTimerAspectTest.someMethod]", super.getAppender().get(1).getMessage());
		assertEquals("PerformanceLoggingAspect after method was called.", super.getAppender().get(2).getMessage());
		assertEquals(ch.qos.logback.classic.Level.INFO, super.getAppender().get(3).getLevel());
	}

	public Method myMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("someMethod");
	}

	public void someMethod() {
		// do nothing
	}
}
