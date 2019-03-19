package gov.va.ocp.framework.transfer.transform;

import static org.junit.Assert.fail;

import org.junit.Test;

import gov.va.ocp.framework.transfer.DomainTransferObjectMarker;
import gov.va.ocp.framework.transfer.PartnerTransferObjectMarker;

public class AbstractDomainToPartnerTest {

	@Test
	public void convertTest() {
		AbstractDomainToPartner transformer = new AbstractDomainToPartner<DomainTransferObjectMarker, PartnerTransferObjectMarker>() {

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
