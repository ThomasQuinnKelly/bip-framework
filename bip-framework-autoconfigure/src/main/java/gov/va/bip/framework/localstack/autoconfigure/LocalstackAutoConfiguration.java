package gov.va.bip.framework.localstack.autoconfigure;

import cloud.localstack.Localstack;
import cloud.localstack.docker.DockerExe;
import cloud.localstack.docker.annotation.LocalstackDockerConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * Performs configuration of a Localstack instance
 *
 */
@Configuration
@EnableConfigurationProperties({ LocalstackProperties.class })
@ConditionalOnProperty(name = "bip.framework.localstack.enabled", havingValue = "true")
public abstract class LocalstackAutoConfiguration {

	@Autowired
	private LocalstackProperties localstackProperties;

	@Value("${localstack.externalHostName:localhost}")
	private String externalHostName;

	@Value("${localstack.imageTag:latest}")
	private String imageTag;

	@Value("${localstack.pullNewImage:true}")
	private boolean pullNewImage;

	@Value("${localstack.randomizePorts:false}")
	private boolean randomizePorts;

	private Map<String, String> environmentVariables = new HashMap<>();

	/**
	 * Start embedded AWS servers on context load
	 *
	 * @throws IOException
	 */
	@PostConstruct
	public void startAwsLocalStack() {
		if (Localstack.INSTANCE != null && Localstack.INSTANCE.getLocalStackContainer() != null) {
			// AWS localstack already running, not trying to re-start
			return;
		} else if (Localstack.INSTANCE != null) {
			// Clean the localstack
			cleanAwsLocalStack();

			Localstack.INSTANCE.startup(buildLocalstackDockerConfiguration());

			configureAwsLocalStack();
		}
	}

	private LocalstackDockerConfiguration buildLocalstackDockerConfiguration() {
		LocalstackDockerConfiguration.LocalstackDockerConfigurationBuilder configBuilder = LocalstackDockerConfiguration.builder();

		configBuilder.externalHostName(externalHostName);
		configBuilder.imageTag(imageTag);
		configBuilder.pullNewImage(pullNewImage);
		configBuilder.randomizePorts(randomizePorts);

		List<LocalstackProperties.Services> listServices = localstackProperties.getServices();

		if (!CollectionUtils.isEmpty(listServices)) {
			// Put selected services into a list
			StringBuilder builder = new StringBuilder();
			for (LocalstackProperties.Services service : listServices) {
				builder.append(service.getName());
				builder.append(":");
				builder.append(service.getPort());
				builder.append(",");
			}

			// Remove last delimiter with setLength.
			builder.setLength(builder.length() - 1);

			String services = String.join(",", builder.toString());
			if (StringUtils.isNotEmpty(services)) {
				// Listed Services will be started
				environmentVariables.put("SERVICES", services);
			}

			configBuilder.environmentVariables(environmentVariables);

			// You can set the ports if needed using properties for each service
			configBuilder.randomizePorts(false);
		}

		return configBuilder.build();
	}

	public abstract void configureAwsLocalStack();

	/**
	 * Stop embedded AWS servers on context destroy
	 */
	@PreDestroy
	public void stopAwsLocalStack() {
		// Stop the localstack
		if (Localstack.INSTANCE != null && Localstack.INSTANCE.getLocalStackContainer() != null) {
			Localstack.INSTANCE.stop();
		}

		// Clean the localstack
		cleanAwsLocalStack();
	}

	/**
	 * Clean AWS Localstack containers
	 */
	private void cleanAwsLocalStack() {
		// Get containers Ids
		DockerExe newDockerExe = new DockerExe();
		String listContainerIds =
				newDockerExe.execute(Arrays.asList("ps", "--no-trunc", "-aq", "--filter", "ancestor=localstack/localstack"));

		// Clean up docker containers
		if (StringUtils.isNotEmpty(listContainerIds)) {
			try {
				String[] splitArray = listContainerIds.split("\\s+");
				for (String containerId : splitArray) {
					newDockerExe.execute(Arrays.asList("rm", "-f", containerId));
				}
			} catch (PatternSyntaxException ex) {
				// PatternSyntaxException During Splitting
			}
		}
	}
}
