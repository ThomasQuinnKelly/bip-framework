package gov.va.bip.framework.security.opa.voter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BipOpaDataResponseTest {


	@Test
	public void testProviderResponse() {
		BipOpaDataResponse bipOpaDataResponse = new BipOpaDataResponse();
		bipOpaDataResponse.setResult(true);
		assertNotNull(bipOpaDataResponse);
		assertTrue(bipOpaDataResponse.getResult());
	}
}
