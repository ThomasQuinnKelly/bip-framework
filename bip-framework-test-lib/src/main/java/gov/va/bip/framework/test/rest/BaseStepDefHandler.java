package gov.va.bip.framework.test.rest;

import java.util.HashMap;
import java.util.Map;

import gov.va.bip.framework.test.service.RESTConfigService;
import gov.va.bip.framework.test.util.RESTUtil;

/**
 * Handler object that extends BaseStepDef to handle rest based api call. Step
 * definition class inject this object thru constructor.
 *
 */
public class BaseStepDefHandler extends BaseStepDef {
	/**
	 * Constructor that calls init method of base class
	 */
	public BaseStepDefHandler() {
		initREST();
	}

	/**
	 * Getter for RESTUtil
	 * 
	 * @return
	 */
	public RESTUtil getRestUtil() {
		return resUtil;
	}

	@Override
	/**
	 * Initialize header map with given key/value pair
	 */
	public void passHeaderInformation(Map<String, String> tblHeader) {
		headerMap = new HashMap<>(tblHeader);
	}

	/**
	 * Getter for header map.
	 * 
	 * @return
	 */
	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	/**
	 * Getter for returning API call response
	 * 
	 * @return
	 */
	public String getStrResponse() {
		return strResponse;
	}

	/**
	 * Getter for RESTConfigService
	 * 
	 * @return
	 */
	public RESTConfigService getRestConfig() {
		return restConfig;
	}

}
