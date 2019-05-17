package gov.va.bip.framework.swagger.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.va.bip.framework.swagger.autoconfigure.SwaggerProperties;

public class SwaggerPropertiesTest {

	@Test
	public void testSetters() {
		SwaggerProperties swaggerProperties = new SwaggerProperties();
		swaggerProperties.setEnabled(false);
		swaggerProperties.setDescription("New Description");
		swaggerProperties.setSecurePaths("New Secure Paths");
		swaggerProperties.setGroupName("New GroupName");
		swaggerProperties.setTitle("New Title");
		swaggerProperties.setVersion("New Version");
		swaggerProperties.setContactName("va.gov");
		swaggerProperties.setContactUrl("https://www.va.gov/");
		swaggerProperties.setContactEmail("test@va.gov");
		swaggerProperties.setLicense("Apache 2.0");
		swaggerProperties.setLicenseUrl("https://www.apache.org/licenses/LICENSE-2.0");
		swaggerProperties.setTermsOfServiceUrl("https://developer.va.gov/terms-of-service");
		assertFalse(swaggerProperties.isEnabled());
		assertEquals("New Description", swaggerProperties.getDescription());
		assertEquals("New Secure Paths", swaggerProperties.getSecurePaths());
		assertEquals("New GroupName", swaggerProperties.getGroupName());
		assertEquals("New Title", swaggerProperties.getTitle());
		assertEquals("New Version", swaggerProperties.getVersion());
		assertEquals("va.gov", swaggerProperties.getContactName());
		assertEquals("https://www.va.gov/", swaggerProperties.getContactUrl());
		assertEquals("test@va.gov", swaggerProperties.getContactEmail());
		assertEquals("Apache 2.0", swaggerProperties.getLicense());
		assertEquals("https://www.apache.org/licenses/LICENSE-2.0", swaggerProperties.getLicenseUrl());
		assertEquals("https://developer.va.gov/terms-of-service", swaggerProperties.getTermsOfServiceUrl());
	}

	@Test
	public void testGetters() {
		SwaggerProperties swaggerProperties = new SwaggerProperties();
		assertTrue(swaggerProperties.isEnabled());
		assertEquals("[Api secure paths via bip.framework.swagger.securePaths]", swaggerProperties.getSecurePaths());
		assertEquals("[Api title via 'bip.framework.swagger.title']", swaggerProperties.getTitle());
		assertEquals("[Api version via 'bip.framework.swagger.version']", swaggerProperties.getVersion());
		assertEquals("", swaggerProperties.getDescription());
		assertEquals("", swaggerProperties.getGroupName());
		assertEquals("", swaggerProperties.getContactName());
		assertEquals("", swaggerProperties.getContactUrl());
		assertEquals("", swaggerProperties.getContactEmail());
		assertEquals("", swaggerProperties.getLicense());
		assertEquals("", swaggerProperties.getLicenseUrl());
		assertEquals("", swaggerProperties.getTermsOfServiceUrl());
	}

}
