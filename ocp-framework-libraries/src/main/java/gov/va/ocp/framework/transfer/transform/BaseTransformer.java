package gov.va.ocp.framework.transfer.transform;

import org.springframework.core.convert.converter.Converter;

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
interface BaseTransformer<S, T> extends Converter<S, T> {
}
