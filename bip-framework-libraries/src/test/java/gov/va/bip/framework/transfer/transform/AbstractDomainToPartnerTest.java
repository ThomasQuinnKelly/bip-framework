package gov.va.bip.framework.transfer.transform;

import static org.junit.Assert.fail;

import org.junit.Test;

import gov.va.bip.framework.transfer.DomainTransferObjectMarker;
import gov.va.bip.framework.transfer.PartnerTransferObjectMarker;
import gov.va.bip.framework.transfer.transform.AbstractDomainToPartner;

public class AbstractDomainToPartnerTest {

	@Test
	public void convertTest() {
		AbstractDomainToPartner<DomainTransferObjectMarker, PartnerTransferObjectMarker> transformer = new AbstractDomainToPartner<DomainTransferObjectMarker, PartnerTransferObjectMarker>() {

			@Override
			public PartnerTransferObjectMarker convert(final DomainTransferObjectMarker domainObject) {
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
