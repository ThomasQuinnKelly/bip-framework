package gov.va.ocp.framework.service;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gov.va.ocp.framework.transfer.DomainTransferObjectMarker;

/**
 * A base Request object capable of representing the payload of a service request.
 *
 * @author jshrader
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceRequest")
public class DomainRequest implements DomainTransferObjectMarker, Serializable {
	private static final long serialVersionUID = -8521125059263688741L;

	/**
	 * Instantiates a new rest request.
	 */
	public DomainRequest() {
		super();
	}

}
