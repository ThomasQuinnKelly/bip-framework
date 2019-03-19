package gov.va.ocp.framework.transfer.transform;

import gov.va.ocp.framework.transfer.DomainTransferObjectMarker;
import gov.va.ocp.framework.transfer.ProviderTransferObjectMarker;

/**
 * The contract for transforming a provider {@link ProviderTransferObjectMarker} object to a domain
 * {@link DomainTransferObjectMarker} object.
 * <p>
 * Implementations should declare the generic parameters with the specific classes involved in the transformation.
 *
 * @param <P> must extend ProviderTransferObjectMarker - the "source" provider object from the provider layer
 * @param <D> must extend DomainTransferObjectMarker - the "target" domain object from the service layer
 *
 * @author aburkholder
 */
public abstract class AbstractProviderToDomain<P extends ProviderTransferObjectMarker, D extends DomainTransferObjectMarker>
		extends AbstractBaseTransformer<P, D> {

	/**
	 * The contract for transforming a {@link ProviderTransferObjectMarker} provider object from the provider layer (the source)
	 * to a {@link DomainTransferObjectMarker} object from the service layer (the target).
	 * <p>
	 * Implementations should declare the generic parameters with the specific classes involved in the transformation.
	 *
	 * @param providerObject the type of the provider object to transform
	 * @return D the type of the transformed equivalent domain object
	 */
	@Override
	public abstract D convert(P providerObject);

}
