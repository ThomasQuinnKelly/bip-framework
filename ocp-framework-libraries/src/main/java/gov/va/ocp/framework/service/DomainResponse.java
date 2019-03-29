package gov.va.ocp.framework.service;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.messages.ServiceMessage;
import gov.va.ocp.framework.transfer.AbstractResponseObject;
import gov.va.ocp.framework.transfer.DomainTransferObjectMarker;

/**
 * A base Response object capable of representing the payload of a service response.
 */
public class DomainResponse extends AbstractResponseObject implements DomainTransferObjectMarker, Serializable {
	private static final long serialVersionUID = -3937937807439785385L;

	/** The serviceMessages. */
	private List<ServiceMessage> serviceMessages;

	/*
	 * cacheResponse
	 *
	 * Must be ignored in the serialization and de-serialization
	 */
	@JsonIgnore
	private boolean doNotCacheResponse = false;

	/**
	 * Instantiates a new rest response.
	 */
	public DomainResponse() {
		super();
	}

	/**
	 * Adds a {@link ServiceMessage} to the serviceMessages list on the response.
	 * <p>
	 * Messages made with this constructor CANNOT be used in a JSR303 context.
	 *
	 * @param severity the severity of the message
	 * @param key the key "code word" for support calls
	 * @param text the text of the message
	 * @param httpStatus the http status associated with the message
	 */
	public final void addMessage(final MessageSeverity severity, final String key, final String text,
			final HttpStatus httpStatus) {
		if (serviceMessages == null) {
			serviceMessages = new LinkedList<>();
		}
		final ServiceMessage serviceMessage = new ServiceMessage();
		serviceMessage.setSeverity(severity);
		serviceMessage.setKey(key);
		serviceMessage.setText(text);
		serviceMessage.setHttpStatus(httpStatus);
		serviceMessages.add(serviceMessage);
	}

	/**
	 * Adds a {@link ServiceMessage} to the serviceMessages list on the response.
	 * <p>
	 * Messages made with this constructor CAN be used in a JSR303 context.
	 *
	 * @param severity the severity of the message
	 * @param key the key "code word" for support calls
	 * @param text the text of the message
	 * @param httpStatus the http status associated with the message
	 * @param paramCount the number of replaceable parameters in the message
	 * @param paramNames the names of the replaceable parameters in the message
	 * @param paramValues the values of the replaceable parameters in the message
	 */
	public final void addMessage(final MessageSeverity severity, final String key, final String text,
			final HttpStatus httpStatus,
			Integer paramCount, String[] paramNames, String[] paramValues) {
		if (serviceMessages == null) {
			serviceMessages = new LinkedList<>();
		}
		final ServiceMessage serviceMessage = new ServiceMessage();
		serviceMessage.setSeverity(severity);
		serviceMessage.setKey(key);
		serviceMessage.setText(text);
		serviceMessage.setHttpStatus(httpStatus);
		serviceMessage.setParamCount(paramCount);
		serviceMessage.setParamNames(paramNames);
		serviceMessage.setParamValues(paramValues);
		serviceMessages.add(serviceMessage);
	}

	/**
	 * Adds all serviceMessages.
	 *
	 * @param newMessages the newMessages
	 */
	public final void addMessages(final List<ServiceMessage> newMessages) {
		if (serviceMessages == null) {
			serviceMessages = new LinkedList<>();
		}
		serviceMessages.addAll(newMessages);
	}

	/**
	 * Gets the serviceMessages.
	 *
	 * @return the serviceMessages
	 */
	public final List<ServiceMessage> getMessages() {
		if (serviceMessages == null) {
			serviceMessages = new LinkedList<>();
		}
		return this.serviceMessages;
	}

	/**
	 * Sets the serviceMessages.
	 *
	 * @param serviceMessages the new serviceMessages
	 */
	public final void setMessages(final List<ServiceMessage> serviceMessages) {
		this.serviceMessages = serviceMessages;
	}

	/**
	 * Checks for serviceMessages of type.
	 *
	 * @param severity the severity
	 * @return true, if successful
	 */
	protected boolean hasMessagesOfType(final MessageSeverity severity) {
		for (final ServiceMessage serviceMessage : getMessages()) {
			if (severity.equals(serviceMessage.getSeverity())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is do not cache response.
	 *
	 * @return true, if is do not cache response
	 */
	public boolean isDoNotCacheResponse() {
		return doNotCacheResponse;
	}

	/**
	 * Sets the do not cache response.
	 *
	 * @param doNotCacheResponse the new do not cache response
	 */
	public void setDoNotCacheResponse(final boolean doNotCacheResponse) {
		this.doNotCacheResponse = doNotCacheResponse;
	}

}