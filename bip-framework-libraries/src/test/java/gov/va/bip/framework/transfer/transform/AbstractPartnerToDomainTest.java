package gov.va.bip.framework.transfer.transform;

import gov.va.bip.framework.transfer.DomainTransferObjectMarker;
import gov.va.bip.framework.transfer.PartnerTransferObjectMarker;
import org.junit.Test;

import static org.junit.Assert.fail;

public class AbstractPartnerToDomainTest {

	@Test
	public void convertTest() {
		AbstractPartnerToDomain<PartnerTransferObjectMarker, DomainTransferObjectMarker> transformer = new AbstractPartnerToDomain<PartnerTransferObjectMarker, DomainTransferObjectMarker>() {

			@Override
			public DomainTransferObjectMarker convert(final PartnerTransferObjectMarker partnerObject) {
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
