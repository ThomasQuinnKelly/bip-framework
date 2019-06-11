package gov.va.bip.framework.test.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.bip.framework.shared.sanitize.Sanitizer;
import gov.va.bip.framework.test.util.PropertiesUtil;

/**
 * A singleton to hold an instance of this class AND - importantly - the test
 * configuration for the project.
 * <p>
 * Future versions of Java and Maven must *always* spin up a new JVM for each
 * integration test, <i><b>across test iterations, and across every
 * artifact</b></i>.
 * <p>
 * Configure the REST controller using {@code config/vetservices*.properties}
 * files. If an environment specific properties file is desired, a System
 * property named {@code test.env} with the name of the environment must exist.
 * If the System test.env propety does not exist, the default properties file
 * will be used.
 * <p>
 * Examples:<br/>
 * If test.env does not exist in System properties<br/>
 * * property filename is {@code config/vetservices.properties}<br/>
 * If test.env exists in System properties<br/>
 * * test.env=ci<br/>
 * &nbsp;&nbsp;&nbsp;- property filename is
 * {@code config/vetservices-ci.properties}<br/>
 * * test.env=stage<br/>
 * &nbsp;&nbsp;&nbsp;- property filename is
 * {@code config/vetservices-stage.properties}<br/>
 *
 * @author aburkholder
 *
 */
public class RESTConfigService {

	/** Logger for this class */
	private static final Logger LOGGER = LoggerFactory.getLogger(RESTConfigService.class);

	/** The singleton instance of this class */
	private static RESTConfigService instance = null;
	/**
	 * The singleton instance of the configuration for the module in which this
	 * artifact is a dependency
	 */
	private Properties prop = null;

	/** The name of the environment in which testing is occurring */
	static final String TEST_ENV = "test.env";

	/** URL regex for use by matchers */

	/**
	 * Do not instantiate
	 */
	private RESTConfigService() {

	}

	/**
	 * Get the configured single instance of the REST controller.
	 *
	 * @return RESTConfigService
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static RESTConfigService getInstance() {
		if (instance == null) {
			instance = new RESTConfigService();
			final String environment = Sanitizer.stripXss(System.getProperty(TEST_ENV));
			String url = "";
			if (StringUtils.isNotBlank(environment)) {
				url = "config/vetservices-inttest-" + environment + ".properties";
			} else {
				url = "config/vetservices-inttest.properties";
			}
			LOGGER.debug("Properties File URL: {}", url);
			LOGGER.debug("Environment: {}", environment);
			final URL urlConfigFile = RESTConfigService.class.getClassLoader().getResource(url);
			LOGGER.info("Properties File ClassLoader URL: {}", urlConfigFile);
			if (urlConfigFile != null) {
				instance.prop = PropertiesUtil.readFile(urlConfigFile);
				LOGGER.debug("Properties File Loaded");
				LOGGER.debug("Properties {}", instance.prop);
			} else {
				LOGGER.warn("No resource found with the URL {}. Property file could not be read.", url);
			}
		}

		return instance;
	}

	/**
	 * Get the value for the specified property name (key). If the key does not
	 * exist, null is returned.
	 *
	 * @param pName
	 *            the property key
	 * @return property the value associated with pName
	 */
	public String getProperty(final String pName) {
		return getProperty(pName, false);
	}

	/**
	 * Get the value for the specified property name (key).
	 * <p>
	 * If the {@code isCheckSystemProp} parameter is {@code true}, then
	 * System.properties will be searched first. If the property does not exist in
	 * the System.properties, then the application properties will be searched.
	 *
	 * @param pName
	 *            the key of the property
	 * @param isCheckSystemProp
	 *            set to {@code true} to first search System.properties
	 * @return String the value associated with pName
	 */
	public String getProperty(final String pName, final boolean isCheckSystemProp) {
		LOGGER.debug("RESTConfigService instance {}", instance);
		if (instance == null) {
			getInstance();
		}
		LOGGER.debug("RESTConfigService instance.prop {}", instance.prop);
		String value = "";
		if (isCheckSystemProp) {
			value = System.getProperty(pName);
		}
		if (StringUtils.isBlank(value) && (instance.prop != null)) {
			LOGGER.debug("Retrieving from Properties File");
			value = instance.prop.getProperty(pName);
		}
		LOGGER.debug("Property Name: {}", pName);
		LOGGER.debug("Property Value: {}", value);
		return value;
	}

}
