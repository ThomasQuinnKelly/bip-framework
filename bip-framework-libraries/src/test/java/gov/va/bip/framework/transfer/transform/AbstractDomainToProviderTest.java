package gov.va.bip.framework.transfer.transform;

import gov.va.bip.framework.transfer.DomainTransferObjectMarker;
import gov.va.bip.framework.transfer.ProviderTransferObjectMarker;
import org.junit.Test;

import static org.junit.Assert.fail;

public class AbstractDomainToProviderTest {

	@Test
	public void convertTest() {
		AbstractDomainToProvider<DomainTransferObjectMarker, ProviderTransferObjectMarker> transformer =
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
