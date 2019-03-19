package gov.va.ocp.framework.transfer.transform;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import gov.va.ocp.framework.transfer.transform.AbstractBaseTransformer.DatatypeFactoryManager;


public class AbstractBaseTransformerTest {

	@Mock
	DatatypeFactoryManager datatypeFactoryManagerMock;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Test
	public void testToDate() throws DatatypeConfigurationException {
		final Date date =
				AbstractBaseTransformer.toDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		assertNotNull(date);
	}

	@Test
	public final void testToDateNull() throws DatatypeConfigurationException {
		final Date date = AbstractBaseTransformer.toDate(null);
		assertNull(date);
	}

	@Test
	public final void testToXMLGregorianCalendar() {
		final XMLGregorianCalendar date = AbstractBaseTransformer.toXMLGregorianCalendar(new Date(), new DatatypeFactoryManager());
		assertNotNull(date);
	}

	@Test
	public final void testToXMLGregorianCalendarExceptionHandling() {
		try {
			when(datatypeFactoryManagerMock.getDatatypeFactory()).thenThrow(new DatatypeConfigurationException());
			assertNull(AbstractBaseTransformer.toXMLGregorianCalendar(new Date(), datatypeFactoryManagerMock));
		} catch (DatatypeConfigurationException e) {
			fail("Either toXMLGregorianCalendar method in the try block did not handle this error or there is something wrong with datatypeFactoryManagerMock");
		}
	}

	@Test
	public final void testGetCurrentDate() {
		final XMLGregorianCalendar date = AbstractBaseTransformer.getCurrentDate(new DatatypeFactoryManager());
		assertNotNull(date);
	}

	@Test
	public final void DatatypeFactoryManagertGetDatatypeFactoryTest() {
		try {
			DatatypeFactoryManager datatypeFactoryManager = new DatatypeFactoryManager();
			assertNotNull(datatypeFactoryManager.getDatatypeFactory());
		} catch (DatatypeConfigurationException e) {
			fail("exception should not be thrown");
		}
	}

}
