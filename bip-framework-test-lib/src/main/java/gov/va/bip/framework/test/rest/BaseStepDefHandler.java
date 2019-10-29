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
	 * Getter for RESTUtil.
	 *
	 * @return the rest util
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
	 * @return the header map
	 */
	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	/**
	 * Getter for returning API call response as string.
	 *
	 * @return the str response
	 */
	public String getStrResponse() {
		return strResponse;
	}

	/**
	 * Getter for returning API call response as object.
	 *
	 * @return the object response
	 */
	public Object getObjResponse() {
		return objResponse;
	}

	/**
	 * Getter for RESTConfigService.
	 *
	 * @return the rest config
	 */
	public RESTConfigService getRestConfig() {
		return restConfig;
	}

}
