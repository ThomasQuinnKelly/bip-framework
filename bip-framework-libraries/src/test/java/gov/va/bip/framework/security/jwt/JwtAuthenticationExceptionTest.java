package gov.va.bip.framework.security.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.security.jwt.JwtAuthenticationException;

public class JwtAuthenticationExceptionTest {

	private static final String[] PARAMS = new String[] { "param1", "param2" };
	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;

	public JwtAuthenticationExceptionTest() {
	}

	@Test
	public void testConstructor() {
		JwtAuthenticationException exception = new JwtAuthenticationException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
		assertNotNull(exception);
	}

	@Test
	public void testConstructorWithThrowableArgument() {
		JwtAuthenticationException exception =
				new JwtAuthenticationException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, new Throwable());
		assertNotNull(exception);
	}

	@Test
	public void testConstructorWithNullKey() {
		JwtAuthenticationException exception =
				new JwtAuthenticationException(null, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, new Throwable());
		assertNotNull(exception);
	}

	@Test
	public final void testGettersAndSetters() {
		JwtAuthenticationException exception =
				new JwtAuthenticationException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, new Throwable(), PARAMS);

		assertTrue(exception.getExceptionData().getKey().equals(TEST_KEY.getKey()));
		assertTrue(exception.getExceptionData().getMessageKey().equals(TEST_KEY));
		assertTrue(exception.getExceptionData().getStatus().equals(HttpStatus.BAD_REQUEST));
		assertTrue(exception.getExceptionData().getSeverity().equals(MessageSeverity.ERROR));

		assertEquals(2, exception.getExceptionData().getParams().length);
		assertTrue(exception.getExceptionData().getParams()[0].equals(PARAMS[0]));
		assertTrue(exception.getExceptionData().getParams()[1].equals(PARAMS[1]));


	}

}
