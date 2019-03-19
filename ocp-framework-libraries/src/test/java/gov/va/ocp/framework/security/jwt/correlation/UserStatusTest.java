package gov.va.ocp.framework.security.jwt.correlation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import gov.va.ocp.framework.exception.OcpRuntimeException;

public class UserStatusTest {

	@Test
	public final void testUserStatus() {
		assertNotNull(UserStatus.ACTIVE);
		assertNotNull(UserStatus.PERMANENT);
		assertNotNull(UserStatus.TEMPORARY);
	}

	@Test
	public final void testValue() {
		assertTrue("A".equals(UserStatus.ACTIVE.value()));
		assertTrue("P".equals(UserStatus.PERMANENT.value()));
		assertTrue("T".equals(UserStatus.TEMPORARY.value()));
	}

	@Test
	public final void testFromValue() {
		assertTrue(UserStatus.fromValue("A").equals(UserStatus.ACTIVE));
		assertTrue(UserStatus.fromValue("P").equals(UserStatus.PERMANENT));
		assertTrue(UserStatus.fromValue("T").equals(UserStatus.TEMPORARY));

		try {
			UserStatus.fromValue("X");
			fail("Should have thrown OcpRuntimeException");
		} catch (Exception e) {
			assertTrue(OcpRuntimeException.class.isAssignableFrom(e.getClass()));
			assertTrue(e.getMessage().startsWith("UserStatus {} does not exist:"));
		}
	}

}
