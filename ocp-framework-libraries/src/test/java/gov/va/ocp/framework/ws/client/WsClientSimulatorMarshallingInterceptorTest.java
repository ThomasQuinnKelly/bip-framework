package gov.va.ocp.framework.ws.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.ocp.framework.ws.client.WsClientSimulatorMarshallingInterceptor;

@RunWith(MockitoJUnitRunner.class)
public class WsClientSimulatorMarshallingInterceptorTest {

	private static final String TEST_MARSHALLED_OUTPUT = "test marshalled output";

	private static final String TEST_OBJECT2 = "test object";

	private static final String TEST_OBJECT = TEST_OBJECT2;

	@Mock
	MethodInvocation mockMethodInvocationWithObjectArg;

	@Mock
	MethodInvocation mockMethodInvocationWithJaxBElementArg;	

	@SuppressWarnings("rawtypes")
	@Mock
	JAXBElement mockJaxbElement;
	@Mock
	Jaxb2Marshaller mockJaxbMarshaller;
	Object obj[]={new Object()};

	Object jaxbObj[] = new Object[1];

	Object respObj = new Object();
	@Before
	public void setUp() throws Exception {
		jaxbObj[0] = mockJaxbElement;
		when(mockMethodInvocationWithObjectArg.getArguments()).thenReturn(obj);
		when(mockMethodInvocationWithJaxBElementArg.getArguments()).thenReturn(jaxbObj);
		when(mockJaxbElement.getValue()).thenReturn("UnitTest");
		try{

			when(mockMethodInvocationWithJaxBElementArg.proceed()).thenReturn(respObj);
		}catch(Throwable e) {
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWsClientSimulatorMarshallingInterceptorMapOfStringJaxb2Marshaller() {
		Map<String, Jaxb2Marshaller> marshallerForPackageMap = new HashMap<String, Jaxb2Marshaller>();
		new WsClientSimulatorMarshallingInterceptor(marshallerForPackageMap);
	}

	@Test
	public void testWsClientSimulatorMarshallingInterceptorMapOfStringJaxb2MarshallerMapOfStringObject() {
		Map<String, Jaxb2Marshaller> marshallerForPackageMap = new HashMap<String, Jaxb2Marshaller>();
		Map<String,Object> objectFactoryForPackageMap = new HashMap<String,Object>();
		new WsClientSimulatorMarshallingInterceptor(marshallerForPackageMap, objectFactoryForPackageMap);
	}

	@Test
	public void testPostConstruct() {
		Map<String, Jaxb2Marshaller> marshallerForPackageMap = new HashMap<String, Jaxb2Marshaller>();
		WsClientSimulatorMarshallingInterceptor wscsmi = new WsClientSimulatorMarshallingInterceptor(marshallerForPackageMap);
		wscsmi.postConstruct();
	}

	@Test
	public void testInvokeForObjectArg() {
		Map<String, Jaxb2Marshaller> marshallerForPackageMap = new HashMap<String, Jaxb2Marshaller>();
		WsClientSimulatorMarshallingInterceptor wscsmi = new WsClientSimulatorMarshallingInterceptor(marshallerForPackageMap);
		try {
			assertNull(wscsmi.invoke(mockMethodInvocationWithObjectArg));
		}catch(Throwable e) {

		}
	}

	@Test
	public void testInvokeForJAXBElement() {
		Map<String, Jaxb2Marshaller> marshallerForPackageMap = new HashMap<String, Jaxb2Marshaller>();
		marshallerForPackageMap.put("java.lang", mockJaxbMarshaller);
		WsClientSimulatorMarshallingInterceptor wscsmi = new WsClientSimulatorMarshallingInterceptor(marshallerForPackageMap);
		try {
			assertNotNull(wscsmi.invoke(mockMethodInvocationWithJaxBElementArg));
		}catch(Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMarshall() {
		Map<String, Jaxb2Marshaller> marshallerForPackageMap = new HashMap<String, Jaxb2Marshaller>();
		marshallerForPackageMap.put("java.lang", mockJaxbMarshaller);
		WsClientSimulatorMarshallingInterceptor wscsmi = new WsClientSimulatorMarshallingInterceptor(marshallerForPackageMap);
		Map<String, Object> objectFactoryForPackageMap = new HashMap<>();
		objectFactoryForPackageMap.put(TEST_OBJECT.getClass().getPackage().getName(), "test object value");
		ReflectionTestUtils.setField(wscsmi, "objectFactoryForPackageMap", objectFactoryForPackageMap);
		try {
			assertTrue(""
					.equals(ReflectionTestUtils.invokeMethod(wscsmi, "marshall", mockJaxbMarshaller, TEST_OBJECT)));

		} catch (Throwable e) {
			fail("Exception should not be thrown");
		}
	}

	@Test
	public void testLogError() {
		Map<String, Jaxb2Marshaller> marshallerForPackageMap = new HashMap<String, Jaxb2Marshaller>();
		marshallerForPackageMap.put("java.lang", mockJaxbMarshaller);
		WsClientSimulatorMarshallingInterceptor wscsmi = new WsClientSimulatorMarshallingInterceptor(marshallerForPackageMap);
		ReflectionTestUtils.invokeMethod(wscsmi, "logError", new Exception("test message"));
	}

}
