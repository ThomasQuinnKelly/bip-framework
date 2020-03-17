package gov.va.bip.framework.security.opa.voter;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class BipOpaDataRequestTest {

	@Test
	public void testProviderRequest() {
		Map<String, Object> input = new HashMap<>();
		input.put("test", "test_value");
		BipOpaDataRequest bipOpaDataRequest = new BipOpaDataRequest(input);
		assertNotNull(bipOpaDataRequest);
		assertNotNull(bipOpaDataRequest.getInput());
	}
}
