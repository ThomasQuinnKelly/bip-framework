package gov.va.ocp.framework.transfer.transform;

import static org.junit.Assert.fail;

import org.junit.Test;

import gov.va.ocp.framework.transfer.DomainTransferObjectMarker;
import gov.va.ocp.framework.transfer.PartnerTransferObjectMarker;

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
