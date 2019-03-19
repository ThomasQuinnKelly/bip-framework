package gov.va.ocp.framework.autoconfigure;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import gov.va.ocp.framework.config.BaseYamlConfig;
import gov.va.ocp.framework.config.OcpCommonSpringProfiles;

/**
 * Fake Spring configuration used to test the partner mock framework classes
 *
 * @author akulkarni
 */
@Configuration
@Profile(OcpCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS)
public class PlatformMockFrameworkTestConfig extends BaseYamlConfig
{

	/** The Constant DEFAULT_PROPERTIES. */
	private static final String DEFAULT_PROPERTIES = "classpath:/application.yml";

	/**
	 * The local environment configuration.
	 */
	@Configuration
	@PropertySource(DEFAULT_PROPERTIES)
	static class DefaultEnvironment extends BaseYamlEnvironment {
	}

}
