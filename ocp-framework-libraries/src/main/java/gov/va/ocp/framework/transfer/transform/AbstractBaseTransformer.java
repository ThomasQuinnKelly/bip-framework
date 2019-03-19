package gov.va.ocp.framework.transfer.transform;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.core.convert.converter.Converter;

import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;

/**
 * This abstract class performs two functions related to OCP object conversion / transformation:
 * <ol>
 * <li>Declare the generic requirement for conversion
 * <li>Provide static methods that are commonly needed for object model transformation.
 * </ol>
 *
 * @param <S> the source object to be converted
 * @param <T> the target object to be created and returned
 * @author aburkholder
 */
public abstract class AbstractBaseTransformer<S, T> implements Converter<S, T> {
	/** Logger */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(AbstractBaseTransformer.class);

	/**
	 * Convert XMLGregorianCalendar to java.util.Date
	 *
	 * @param xmlDate
	 * @return Date
	 */
	public static Date toDate(final XMLGregorianCalendar xmlDate) {
		if (xmlDate == null) {
			return null;
		}
		return xmlDate.toGregorianCalendar().getTime();
	}

	/**
	 * convert java.util.Date to XMLGregorianCalendar
	 *
	 * @param date the java.util.Date
	 * @param datatypeFactoryManager the object that is used to get the datatype factory (added to ease testing, see
	 *            {@link DatatypeFactoryManager})
	 * @return XMLGregorianCalendar object
	 */
	public static XMLGregorianCalendar toXMLGregorianCalendar(final Date date, final DatatypeFactoryManager datatypeFactoryManager) {
		try {
			final GregorianCalendar gc = new GregorianCalendar(Locale.getDefault());
			gc.setTimeInMillis(date.getTime());
			return datatypeFactoryManager.getDatatypeFactory().newXMLGregorianCalendar(gc);

		} catch (final DatatypeConfigurationException dcEx) { // NOSONAR
			LOGGER.error(dcEx.getMessage());
			return null;
		}
	}

	/**
	 * Convert "now" to XMLGregorianCalendar
	 *
	 * @param datatypeFactoryManager the object that is used to get the datatype factory (added to ease testing, see
	 *            {@link DatatypeFactoryManager})
	 * @return XMLGregorianCalendar
	 */
	public static XMLGregorianCalendar getCurrentDate(final DatatypeFactoryManager datatypeFactoryManager) {
		try {
			return datatypeFactoryManager.getDatatypeFactory().newXMLGregorianCalendar(new GregorianCalendar(Locale.getDefault()));

		} catch (final DatatypeConfigurationException dcEx) { // NOSONAR
			LOGGER.error(dcEx.getMessage());
			return null;
		}
	}

	/**
	 * This class is added to substitute for the static method javax.xml.datatype.DatatypeFactory.newInstance() to aid in testing. The
	 * class can be mocked and an exception can be thrown while calling the static method to test the exception handling code. The call
	 * to the static method is wrapped in getDatatypeFactory() method in this class.
	 *
	 */
	public static class DatatypeFactoryManager {
		/**
		 * This method is a wrapper for the static method javax.xml.datatype.DatatypeFactory.newInstance() call. While testing, a mock
		 * object could deliberately throw an exception to test the error handling code.
		 *
		 * @return a DatatypeFactory instance
		 * @throws DatatypeConfigurationException
		 */
		public DatatypeFactory getDatatypeFactory() throws DatatypeConfigurationException {
			return DatatypeFactory.newInstance();
		}
	}
}
