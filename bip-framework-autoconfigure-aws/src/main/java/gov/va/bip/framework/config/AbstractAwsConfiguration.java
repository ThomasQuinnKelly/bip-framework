package gov.va.bip.framework.config;

import com.amazonaws.auth.*;
import gov.va.bip.framework.localstack.autoconfigure.LocalstackAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public abstract class AbstractAwsConfiguration {

	@Value("${bip.framework.localstack.enabled:false}")
	boolean localstackEnabled;

	@SuppressWarnings("unused")
	@Autowired(required = false)
	public LocalstackAutoConfiguration localstackAutoConfiguration;

	boolean isEmbeddedAws = false;
	boolean isLocalInt = false;

	protected AWSCredentialsProvider createAwsCredentialsProvider(final String localAccessKey, final String localSecretKey) {

		AWSCredentialsProvider ec2ContainerCredentialsProvider = new EC2ContainerCredentialsProviderWrapper();

		if (StringUtils.isEmpty(localAccessKey) || StringUtils.isEmpty(localSecretKey)) {
			return ec2ContainerCredentialsProvider;
		}

		AWSCredentialsProvider localAwsCredentialsProvider =
				new AWSStaticCredentialsProvider(new BasicAWSCredentials(localAccessKey, localSecretKey));

		return new AWSCredentialsProviderChain(localAwsCredentialsProvider, ec2ContainerCredentialsProvider);
	}

	public boolean isLocalstackEnabled() {
		return localstackEnabled;
	}

	public void setLocalstackEnabled(boolean localstackEnabled) {
		this.localstackEnabled = localstackEnabled;
	}

	public boolean isEmbeddedAws() {
		return isEmbeddedAws;
	}

	public void setEmbeddedAws(boolean embeddedAws) {
		isEmbeddedAws = embeddedAws;
	}

	public boolean isLocalInt() {
		return isLocalInt;
	}

	public void setLocalInt(boolean localInt) {
		isLocalInt = localInt;
	}
}
