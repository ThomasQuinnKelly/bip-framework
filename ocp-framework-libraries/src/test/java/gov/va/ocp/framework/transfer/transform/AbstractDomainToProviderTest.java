package gov.va.ocp.framework.transfer.transform;

import static org.junit.Assert.fail;

import org.junit.Test;

import gov.va.ocp.framework.transfer.DomainTransferObjectMarker;
import gov.va.ocp.framework.transfer.ProviderTransferObjectMarker;

public class AbstractDomainToProviderTest {

	@Test
	public void convertTest() {
		AbstractDomainToProvider transformer =
				new AbstractDomainToProvider<DomainTransferObjectMarker, ProviderTransferObjectMarker>() {

					@Override
					public ProviderTransferObjectMarker convert(final DomainTransferObjectMarker domainObject) {
						return null;
					}
				};
		try {
			transformer.convert(null);
		} catch (Exception e) {
			fail("exception should not be thrown");
		}
	}

}
