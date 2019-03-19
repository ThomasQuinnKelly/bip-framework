package gov.va.ocp.framework.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.messages.ServiceMessage;

public class DomainResponseTest {

	private DomainResponse mockServiceResponse;
	private List<ServiceMessage> testMessages = new ArrayList<ServiceMessage>();
	ServiceMessage infoMessage;
	ServiceMessage warnMessage;
	ServiceMessage errorMessage;
	ServiceMessage fatalMessage;

	@Before
	public void setUp() throws Exception {
		mockServiceResponse = new DomainResponse();
		infoMessage = new ServiceMessage(MessageSeverity.INFO, "InfoKey", "Dummy info text", null);
		warnMessage = new ServiceMessage(MessageSeverity.WARN, "WarnKey", "Dummy warning text", null);
		errorMessage = new ServiceMessage(MessageSeverity.ERROR, "ErrorKey", "Dummy error text", null);
		fatalMessage = new ServiceMessage(MessageSeverity.FATAL, "FatalKey", "Dummy fatal text", null);
		addTestMessages();
	}

	private void addTestMessages() {
		testMessages.add(infoMessage);
		testMessages.add(warnMessage);
		testMessages.add(errorMessage);
		testMessages.add(fatalMessage);
	}

	@Test
	public void testAddMessageWithNullMessages() {

		mockServiceResponse.setMessages(null);
		mockServiceResponse.addMessage(MessageSeverity.INFO, "InfoKey", "Dummy info text", null,
				1, new String[] { "pName" }, new String[] { "pValue" });

		assertNotNull(mockServiceResponse.getMessages());
		assertEquals(1, mockServiceResponse.getMessages().size());

	}

	@Test
	public void testAddMessageWithParams() {
		mockServiceResponse.addMessage(MessageSeverity.INFO, "InfoKey", "Dummy info text", null,
				1, new String[] { "pName" }, new String[] { "pValue" });
		assertNotNull(mockServiceResponse.getMessages());
		assertEquals(1, mockServiceResponse.getMessages().size());

	}

	@After
	public void tearDown() throws Exception {
		testMessages.clear();
	}

	@Test
	public void testAddMessage() {
		mockServiceResponse.addMessage(MessageSeverity.INFO, "InfoKey", "Dummy info text", null);
		assertNotNull(mockServiceResponse.getMessages());
		assertEquals(1, mockServiceResponse.getMessages().size());

	}

	@Test
	public void testAddMessages() {
		mockServiceResponse.addMessages(testMessages);
		assertNotNull(mockServiceResponse.getMessages());
		assertEquals(4, mockServiceResponse.getMessages().size());
	}

	@Test
	public void testSetMessages() {
		mockServiceResponse.setMessages(testMessages);
		DomainResponse serviceResponseForEqualsTest = new DomainResponse();
		assertFalse(mockServiceResponse.getMessages().equals(serviceResponseForEqualsTest.getMessages()));
		serviceResponseForEqualsTest.setMessages(testMessages);
		assertNotNull(mockServiceResponse.getMessages().equals(serviceResponseForEqualsTest.getMessages()));
		assertNotNull(mockServiceResponse.getMessages());
		assertEquals(4, mockServiceResponse.getMessages().size());
	}

	@Test
	public void testHasFatals() {
		mockServiceResponse.setMessages(testMessages);
		assertTrue(mockServiceResponse.hasFatals());
	}

	@Test
	public void testHasErrors() {
		mockServiceResponse.setMessages(testMessages);
		assertTrue(mockServiceResponse.hasErrors());
	}

	@Test
	public void testHasWarnings() {
		mockServiceResponse.setMessages(testMessages);
		assertTrue(mockServiceResponse.hasWarnings());
	}

	@Test
	public void testHasInfos() {
		mockServiceResponse.setMessages(testMessages);
		assertTrue(mockServiceResponse.hasInfos());
	}

}
