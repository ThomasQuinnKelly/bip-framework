package gov.va.bip.framework.rest.provider;

import java.io.Serializable;

import gov.va.bip.framework.transfer.ProviderTransferObjectMarker;

/**
 * A base Request object capable of representing the payload of a provider request.
 *
 * @see ProviderTransferObjectMarker
 *
 */
public class ProviderRequest implements ProviderTransferObjectMarker, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new rest provider request.
	 */
	public ProviderRequest() {
		super();
	}
}