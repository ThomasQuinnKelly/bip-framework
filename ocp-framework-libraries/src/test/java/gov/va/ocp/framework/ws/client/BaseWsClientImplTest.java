package gov.va.ocp.framework.ws.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.va.ocp.framework.ws.client.BaseWsClientImpl;

public class BaseWsClientImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		new TestBaseWsClient();
	}

}
class TestBaseWsClient extends BaseWsClientImpl {
	
	TestBaseWsClient() {
		super();
	}
}