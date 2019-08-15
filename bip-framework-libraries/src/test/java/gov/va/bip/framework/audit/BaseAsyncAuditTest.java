package gov.va.bip.framework.audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class BaseAsyncAuditTest {

	@Test
	public void postConstructTest() {
		BaseAsyncAudit baseAsyncAudit = new BaseAsyncAudit();
		AuditLogSerializer auditLogSerializer = new AuditLogSerializer();
		ReflectionTestUtils.setField(baseAsyncAudit, "auditLogSerializer", auditLogSerializer);
		baseAsyncAudit.postConstruct();
	}

	@Test
	public void closeInputStreamIfRequiredTest() {
		InputStream mockInputstream = mock(InputStream.class);
		BaseAsyncAudit.closeInputStreamIfRequired(mockInputstream);
		try {
			verify(mockInputstream, times(1)).close();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Problem testing input stream closing");
		}
	}

	@Test
	public void convertBytesOfSetSizeToStringTest() {
		try {
			InputStream stubInputStream = 
					IOUtils.toInputStream("some test data for my input stream", "UTF-8");
			String convertedString = BaseAsyncAudit.convertBytesOfSetSizeToString(stubInputStream);
			assertEquals("some test data for my input stream", convertedString);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Problem testing convert bytes of set size to string");
		}
	}
	
	@Test
	public void convertBytesOfSetSizeToStringLargeTextTest() {
		try {
			InputStream stubInputStream = 
					IOUtils.toInputStream("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel nisl placerat, "
							+ "sodales odio eget, commodo nulla. In tempus faucibus leo non dapibus. Proin id fringilla elit, eu gravida metus. Nulla porttitor eros id sem egestas efficitur. "
							+ "Curabitur nibh magna, fermentum ut eros ac, sagittis vestibulum felis. Vestibulum laoreet non enim eu faucibus. Fusce laoreet est non turpis hendrerit, mollis porta quam iaculis. "
							+ "Etiam ut bibendum quam. Cras ac facilisis erat, eu sodales est. Cras diam erat, egestas non dignissim ac, eleifend ac neque. Donec imperdiet tristique turpis eget tristique. "
							+ "Fusce mauris mauris, ultricies eu justo quis, efficitur mattis massa. Mauris urna orci, eleifend et dui ac, blandit maximus quam. Sed eget gravida orci, ut vulputate enim. "
							+ "Curabitur nec sapien ultricies, congue sapien id, tincidunt leo. Integer imperdiet arcu eu lectus bibendum, in consectetur lacus fermentum. Curabitur tempus lobortis mattis. "
							+ "Aliquam sodales posuere elementum. Curabitur auctor, ipsum at gravida ultrices amet. Aliquam sodales posuere elementum", "UTF-8");
			String convertedString = BaseAsyncAudit.convertBytesOfSetSizeToString(stubInputStream);
			assertNotNull(convertedString);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Problem testing convert bytes of set size to string");
		}
	}
	
	@Test
	public void convertBytesOfSetSizeToStringTestNull() {
		String convertedString = BaseAsyncAudit.convertBytesOfSetSizeToString(null);
		assertEquals("", convertedString);

	}
}
