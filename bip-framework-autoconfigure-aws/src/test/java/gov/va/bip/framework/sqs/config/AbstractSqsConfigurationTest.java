package gov.va.bip.framework.sqs.config;

import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.mock.env.MockEnvironment;

import javax.jms.ConnectionFactory;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class AbstractSqsConfigurationTest {

	/**
	 * Test of destinationResolver method, of class AbstractSqsConfiguration.
	 */
	@Test
	public void testDestinationResolver() {
		final SqsProperties sqsProperties = new SqsProperties();
		sqsProperties.setEndpoint("kttp://localhost:8080/endpoint");
		final AbstractSqsConfiguration instance = new AbstractSqsConfigurationImpl();
		final DestinationResolver result = instance.destinationResolver(sqsProperties);
		assertNotNull(result);

	}

	/**
	 * Test of jmsTemplate method, of class AbstractSqsConfiguration.
	 */
	@Test
	public void testJmsTemplate() {
		final SqsProperties sqsProperties = new SqsProperties();
		sqsProperties.setEndpoint("kttp://localhost:8080/endpoint");
		final AbstractSqsConfiguration instance = new AbstractSqsConfigurationImpl();
		final JmsTemplate result = instance.jmsTemplate(sqsProperties, mock(ConnectionFactory.class));
		assertNotNull(result);
	}


	@Test
	public void testCreateStandardSQSConnectionFactory() {
		final SqsProperties sqsProperties = new SqsProperties();
		sqsProperties.setEndpoint("kttp://localhost:8080/endpoint");
		sqsProperties.setRegion("us-west-2");

		final AbstractSqsConfiguration instance = new AbstractSqsConfigurationImpl();
		final Environment env = new MockEnvironment();
		instance.environment = env;

		final ConnectionFactory factory = instance.createStandardSQSConnectionFactory(sqsProperties);
		assertNotNull(factory);
	}

	public class AbstractSqsConfigurationImpl extends AbstractSqsConfiguration {

		@Override
		public ConnectionFactory connectionFactory(final SqsProperties sqsProperties) {
			return null;
		}
	}

}
