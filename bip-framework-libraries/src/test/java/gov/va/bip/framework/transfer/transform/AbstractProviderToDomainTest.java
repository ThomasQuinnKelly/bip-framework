package gov.va.bip.framework.transfer.transform;

import static org.junit.Assert.fail;

import org.junit.Test;

import gov.va.bip.framework.transfer.DomainTransferObjectMarker;
import gov.va.bip.framework.transfer.ProviderTransferObjectMarker;
import gov.va.bip.framework.transfer.transform.AbstractProviderToDomain;

public class AbstractProviderToDomainTest {

	@Test
	public void convertTest() {
		AbstractProviderToDomain<ProviderTransferObjectMarker, DomainTransferObjectMarker> transformer =
				new AbstractProviderToDomain<ProviderTransferObjectMarker, DomainTransferObjectMarker>() {

					@Override
					public DomainTransferObjectMarker convert(final ProviderTransferObjectMarker providerObject) {
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
