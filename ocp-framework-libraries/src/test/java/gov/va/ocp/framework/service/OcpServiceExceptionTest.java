package gov.va.ocp.framework.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

public class OcpServiceExceptionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEmptyConstructor() {
		OcpServiceException ocpServiceException = new OcpServiceException(null, null, null, null, null);
		assertNull(ocpServiceException.getKey());
		assertNull(ocpServiceException.getMessage());
		assertNull(ocpServiceException.getSeverity());
		assertNull(ocpServiceException.getStatus());
		assertNull(ocpServiceException.getCause());
	}

	@Test
	public void testPopulatedConstructor() {
		OcpServiceException ocpServiceException =
				new OcpServiceException("some.key", "Unit Testing", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
						new Throwable("test"));
		assertNotNull(ocpServiceException.getKey());
		assertNotNull(ocpServiceException.getMessage());
		assertNotNull(ocpServiceException.getSeverity());
		assertNotNull(ocpServiceException.getStatus());
		assertNotNull(ocpServiceException.getCause());
	}

	@Test
	public void testPopulatedNoCauseConstructor() {
		OcpServiceException ocpServiceException =
				new OcpServiceException("some.key", "Unit Testing", MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
		assertNotNull(ocpServiceException.getKey());
		assertNotNull(ocpServiceException.getMessage());
		assertNotNull(ocpServiceException.getSeverity());
		assertNotNull(ocpServiceException.getStatus());
		assertNull(ocpServiceException.getCause());
	}

}
