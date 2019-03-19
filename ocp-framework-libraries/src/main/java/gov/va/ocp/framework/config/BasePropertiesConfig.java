package gov.va.ocp.framework.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.properties.OcpPropertySourcesPlaceholderConfigurer;

/**
 * Abstract baseclass for Spring configuration of the properties files
 *
 * @author Jon Shrader
 */
public class BasePropertiesConfig {

	/**
	 * AbstractPropertiesEnvironment, parent of all our properties environments.
	 */
	public static class BasePropertiesEnvironment {

		/**
		 * Post construct called after spring initialization completes.
		 */
		@PostConstruct
		public final void postConstruct() {
			LOGGER.info("Loading environment: " + this.getClass().getName());
		}
	}

	/** logger for this class. */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(BasePropertiesConfig.class);

	/** The Constant CLASSPATH_PREFIX. */
	public static final String CLASSPATH_PREFIX = "classpath:/";

	/** The Constant CLASSPATH_CONFIG_PREFIX. */
	public static final String CLASSPATH_CONFIG_PREFIX = CLASSPATH_PREFIX + "config/";

	/** The Constant DASH. */
	public static final String DASH = "-";

	/** The Constant PROPERTIES_FILE_EXT. */
	public static final String PROPERTIES_FILE_EXT = ".properties";

	/**
	 * protected utility class constructor.
	 */
	protected BasePropertiesConfig() {
	}

	/**
	 * properties bean
	 *
	 * @return the property sources placeholder configurer
	 */
	@Bean(name = "properties")
	static PropertySourcesPlaceholderConfigurer properties() {
		return new OcpPropertySourcesPlaceholderConfigurer();
	}

}
