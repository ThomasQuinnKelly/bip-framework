package gov.va.bip.framework.swagger.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.classmate.TypeResolver;

import gov.va.bip.framework.swagger.autoconfigure.BipSwaggerAutoConfiguration;
import gov.va.bip.framework.swagger.autoconfigure.SwaggerProperties;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@RunWith(SpringRunner.class)
public class BipSwaggerAutoConfigurationTest {

	@Mock
	private SwaggerProperties swaggerProperties = new SwaggerProperties();

	@Mock
	private TypeResolver typeResolver;

	@InjectMocks
	BipSwaggerAutoConfiguration bipSwaggerAutoConfiguration;

	@Test
	public void swaggerAutoConfigurationTest() throws Exception {
		Docket docket = bipSwaggerAutoConfiguration.categoryApi();
		assertEquals("default", docket.getGroupName());
		DocumentationType documentationType = docket.getDocumentationType();
		assertEquals("swagger", documentationType.getName());
		assertEquals("2.0", documentationType.getVersion());
		assertEquals("application", documentationType.getMediaType().getType());
		assertEquals("json", documentationType.getMediaType().getSubtype());
		assertEquals(0, documentationType.getMediaType().getParameters().size());
		assertNotNull(bipSwaggerAutoConfiguration.categoryApi());
	}

}
