package gov.va.bip.framework.hystrix.autoconfigure;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class RequestAttributeAwareCallableWrapperTest {

	private static final String TEST_RETURN_VALUE = "test return value";

	private static final String REQUEST_ID = "RequestId";

	private String requestId;

	@Before
	public void setUp() throws Exception {

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
		requestId = UUID.randomUUID().toString();
	}

	@Test(expected = Exception.class)
	public void shouldPropagateRequestAttributes() {

		// given
		RequestContextHolder.currentRequestAttributes().setAttribute(REQUEST_ID, requestId, SCOPE_REQUEST);

		// when
		final Object result = new HystrixCommand<Object>(HystrixCommandGroupKey.Factory.asKey("TestGroupKey")) {
			@Override
			protected Object run() throws Exception {
				return RequestContextHolder.currentRequestAttributes().getAttribute(REQUEST_ID, SCOPE_REQUEST);
			}
		}.execute();

		// then
		assertEquals(requestId, result);
	}

	@Test
	public void wrapCallableTest() {
		RequestAttributeAwareCallableWrapper requestAttributeAwareCallableWrapper = new RequestAttributeAwareCallableWrapper();
		Callable<String> callable = new Callable<String>() {

			@Override
			public String call() throws Exception {
				return TEST_RETURN_VALUE;
			}
		};
		try {
			org.junit.Assert.assertTrue(TEST_RETURN_VALUE.equals(requestAttributeAwareCallableWrapper.wrapCallable(callable).call()));
		} catch (Exception e) {
			fail("No exception expected while invoking call() method on the return value of wrapCallable() method on requestAttributeAwareCallableWrapper");
		}
	}

	@Configuration
	@EnableAutoConfiguration
	public static class Application {

		@Bean
		public HystrixCallableWrapper requestAttributeAwareCallableWrapper() {
			return new RequestAttributeAwareCallableWrapper();
		}
	}

}