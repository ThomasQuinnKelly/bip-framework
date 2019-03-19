package gov.va.ocp.framework.messages;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * ServiceMessage is a generic abstraction of a "message" or "notification" which is layer agnostic and can be used to communicate
 * status or
 * other sorts of information during method calls between components/layers. This is serializable and can be used in SOAP or REST
 * calls. This class has param names and valsues as lists.
 * <p>
 * This class can be extended to provide expression names and values used in messages
 * that have replaceable parameters, e.g. "Some {0} message with {1} parameters".
 * <p>
 * Current use-case is for message transmission back to JSR303 validation constraint messages.
 *
 * @author vanapalliv
 */
public abstract class AbstractMessage implements Serializable {

	/** The Constant serialVersisonUID. */
	private static final long serialVersionUID = -1711431368372127556L;

	private Integer parameterCount = 0; // NOSONAR cannot be final

	private String[] parameterNames; // NOSONsAR cannot be final

	private String[] parameterValues; // NOSONAR cannot be final

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private Date timestamp; // NOSONAR cannot be final

	/**
	 * Construct a message providing only replaceable parameters.
	 *
	 * @param paramCount
	 * @param paramNames
	 * @param paramValues
	 */
	// NOSONAR not duplicate
	public AbstractMessage(Integer paramCount, String[] paramNames, String[] paramValues) {
		super();
		this.parameterCount = paramCount;
		this.parameterNames = paramNames;
		this.parameterValues = paramValues;
		this.timestamp = new Date();
	}

	/**
	 * Construct default (empty) message object.
	 */
	// NOSONAR not duplicate
	public AbstractMessage() {
		super();
		this.timestamp = new Date();
	}

	/**
	 * An in-order array of replaceable parameter names used in the message.
	 * These are the literal strings between the curly braces.
	 *
	 * @return String[] the names, in same order as thier respective getParamValues()
	 */
	@JsonIgnore
	@JsonProperty(value = "parameterNames")
	public String[] getParamNames() {  	// NOSONAR not duplicate
		return parameterNames;
	}

	/**
	 * An in-order array of replaceable parameter names used in the message.
	 * These are the literal strings between the curly braces.
	 *
	 * @param paramNames the names, in same order as thier respective getParamValues()
	 */
	@ApiModelProperty(hidden = true)
	public void setParamNames(String[] paramNames) {  	// NOSONAR not duplicate
		this.parameterNames = paramNames;
	}

	/**
	 * An in-order array of replaceable parameter values used in the message.
	 * These are the values that replace the parameters in the message.
	 *
	 * @return String[] the values, in same order as their respective getParamNames()
	 */
	@JsonIgnore
	@JsonProperty(value = "parameterValues")
	public String[] getParamValues() {  	// NOSONAR not duplicate
		return parameterValues;
	}

	/**
	 * An in-order array of replaceable parameter values used in the message.
	 * These are the values that replace the parameters in the message.
	 *
	 * @param paramValues the values, in same order as their respective getParamNames()
	 */
	@ApiModelProperty(hidden = true)
	public void setParamValues(String[] paramValues) { 	// NOSONAR not duplicate
		this.parameterValues = paramValues;
	}

	/**
	 * Number of elements in the getParamNames() and getParamValues() arrays.
	 *
	 * @return Integer the number of elements in the arrays
	 */
	@JsonIgnore
	@JsonProperty(value = "parameterCount")
	public Integer getParamCount() {  	// NOSONAR not duplicate
		return parameterCount;
	}

	/**
	 * Number of elements in the getParamNames() and getParamValues() arrays.
	 *
	 * @param paramCount the number of elements in the arrays
	 */
	@ApiModelProperty(hidden = true)
	public void setParamCount(Integer paramCount) {  	// NOSONAR not duplicate
		this.parameterCount = paramCount;
	}

	/**
	 * Gets the timestamp to be part of message payload.
	 *
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp for the message payload.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}