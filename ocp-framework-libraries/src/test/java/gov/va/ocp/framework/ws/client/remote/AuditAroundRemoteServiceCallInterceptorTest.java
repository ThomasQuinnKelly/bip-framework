package gov.va.ocp.framework.ws.client.remote;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import gov.va.ocp.framework.AbstractBaseLogTester;
import gov.va.ocp.framework.audit.AuditLogSerializer;
import gov.va.ocp.framework.ws.client.remote.AuditAroundRemoteServiceCallInterceptor;


@RunWith(MockitoJUnitRunner.class)
public class AuditAroundRemoteServiceCallInterceptorTest extends AbstractBaseLogTester {


	
	@Mock
	MethodInvocation methodInvocation;
	
	@Mock
	AuditLogSerializer asyncLogging;
	
	@InjectMocks
	AuditAroundRemoteServiceCallInterceptor auditAroundRemoteServiceCallInterceptor;

	@Before
	public void setUp() {
		auditAroundRemoteServiceCallInterceptor = new AuditAroundRemoteServiceCallInterceptor();
		MockitoAnnotations.initMocks(this);
	}

	@Override
	@After
	public void tearDown() {
	}

	
	@Test
	public void testInvoke() throws Throwable {
		

		Object[] args = new Object[3];
		args[0] = "0";
		args[1] = "1"; 
		args[2] = "2";
		when(methodInvocation.getArguments()).thenReturn(args);
		when(methodInvocation.getMethod()).thenReturn(Helper.class.getMethod("getString"));
	
		assertNull(auditAroundRemoteServiceCallInterceptor.invoke(methodInvocation));

	}
	
	@Test(expected=Exception.class)
	public void testInvokeException() throws Throwable {
		

		Object[] args = new Object[3];
		args[0] = "0";
		args[1] = "1"; 
		args[2] = "2";
		//doThrow(new Exception("Exception Test")).when(methodInvocation.proceed());
		when(methodInvocation.getArguments()).thenReturn(args);
		when(methodInvocation.getMethod()).thenReturn(Helper.class.getMethod("getString"));
		when(methodInvocation.proceed()).thenThrow(new Exception("Exception Test"));
		auditAroundRemoteServiceCallInterceptor.invoke(methodInvocation);
	}


	interface Helper {

		String getString();

	}


}
