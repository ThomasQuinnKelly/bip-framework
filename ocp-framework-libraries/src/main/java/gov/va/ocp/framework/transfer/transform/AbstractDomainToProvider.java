package gov.va.ocp.framework.transfer.transform;

import gov.va.ocp.framework.transfer.DomainTransferObjectMarker;
import gov.va.ocp.framework.transfer.ProviderTransferObjectMarker;

/**
 * The contract for transforming a domain {@link DomainTransferObjectMarker} object to a provider
 * {@link ProviderTransferObjectMarker} object.
 * <p>
 * Implementations should declare the generic parameters with the specific classes involved in the transformation.
 *
 * @param <D> must extend DomainTransferObjectMarker - the "source" domain object from the service layer
 * @param <P> must extend ProviderTransferObjectMarker - the "target" provider object from the provider layer
 *
 * @author aburkholder
 */
public abstract class AbstractDomainToProvider<D extends DomainTransferObjectMarker, P extends ProviderTransferObjectMarker>
		extends AbstractBaseTransformer<D, P> {

	/**
	 * The contract for transforming a {@link DomainTransferObjectMarker} object from the service layer (the source)
	 * to a {@link ProviderTransferObjectMarker} provider object from the provider layer (the target).
	 * <p>
	 * Implementations should declare the generic parameters with the specific classes involved in the transformation.
	 *
	 * @param domainObject the type of the domain object to transform
	 * @return P the type of the transformed equivalent provider object
	 */
	@Override
	public abstract P convert(D domainObject);

}
