package gov.va.ocp.framework.rest.provider.aspect;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class BaseHttpProviderAspectTest {

	@Test
	public void testRestController() {
		BaseHttpProviderAspect.restController();
	}

	@Test
	public void testPublicServiceResponseRestMethod() {
		BaseHttpProviderAspect.publicServiceResponseRestMethod();
	}

	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;
	@Mock
	private MethodSignature signature;

	@Mock
	private JoinPoint.StaticPart staticPart;
	private Object[] value;

	@Before
	public void setUp() throws Exception {
		value = new Object[1];
		value[0] = "";
		Mockito.lenient().when(proceedingJoinPoint.getArgs()).thenReturn(value);
		Mockito.lenient().when(proceedingJoinPoint.getStaticPart()).thenReturn(staticPart);
		Mockito.lenient().when(proceedingJoinPoint.getSignature()).thenReturn(signature);
		Mockito.lenient().when(signature.getMethod()).thenReturn(myMethod());
	}

	/**
	 * Test of auditableAnnotation method, of class BaseHttpProviderAspect.
	 */
	@Test
	public void testAuditableAnnotation() {
		BaseHttpProviderAspect.auditableAnnotation();
	}

	/**
	 * Test of auditableExecution method, of class BaseHttpProviderAspect.
	 */
	@Test
	public void testAuditableExecution() {
		BaseHttpProviderAspect.auditableExecution();
	}

	/**
	 * Test of auditRestController method, of class BaseHttpProviderAspect.
	 */
	@Test
	public void testAuditRestController() {
		BaseHttpProviderAspect.restController();
	}

	@Test
	public void testPopulateHeadersMap() throws Exception {
		BaseHttpProviderAspect baseHttpProviderAspect = new BaseHttpProviderAspect();
		HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
		when(httpServletResponse.getHeader("contentType")).thenReturn("text/xml");
		when(httpServletResponse.getHeader("Accept")).thenReturn("text/xml");
		Map<String, String> headersToBePopulated = new HashMap<>();
		Collection<String> listOfHeaderNames = new LinkedList<>();
		listOfHeaderNames.add("contentType");
		listOfHeaderNames.add("Accept");
		ReflectionTestUtils.invokeMethod(baseHttpProviderAspect, "populateHeadersMap", httpServletResponse, headersToBePopulated,
				listOfHeaderNames);
		assertTrue(headersToBePopulated.get("contentType").equals("text/xml"));
		assertTrue(headersToBePopulated.get("Accept").equals("text/xml"));
	}

	public Method myMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("someMethod");
	}

	public void someMethod() {
		// do nothing
	}

}
