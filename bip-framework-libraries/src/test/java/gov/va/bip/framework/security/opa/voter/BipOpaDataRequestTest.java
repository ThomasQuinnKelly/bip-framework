package gov.va.bip.framework.security.opa.voter;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class BipOpaDataRequestTest {


	@Test
	public void testProviderRequest() {
		Map<String, Object> input = new HashMap<>();
		input.put("test","test_value");
		BipOpaDataRequest bipOpaDataRequest = new BipOpaDataRequest(input);
		assertNotNull(bipOpaDataRequest);
		assertNotNull(bipOpaDataRequest.getInput());
	}
}
