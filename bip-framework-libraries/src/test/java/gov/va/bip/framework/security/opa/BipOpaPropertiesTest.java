package gov.va.bip.framework.security.opa;

import gov.va.bip.framework.security.config.BipSecurityTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BipSecurityTestConfig.class)
public class BipOpaPropertiesTest {

	@Autowired
	BipOpaProperties bipOpaProperties;

	public BipOpaPropertiesTest() {
	}

	/**
	 * Test of isEnabled method, of class BipOpaProperties.
	 */
	@Test
	public void testIsEnabled() {
		boolean expResult = false;
		boolean result = bipOpaProperties.isEnabled();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setEnabled method, of class BipOpaProperties.
	 */
	@Test
	public void testSetEnabled() {
		boolean enabled = true;
		bipOpaProperties.setEnabled(enabled);
		assertTrue(bipOpaProperties.isEnabled());
	}

	/**
	 * Test of isAllVotersAbstainGrantAccess method, of class BipOpaProperties.
	 */
	@Test
	public void testIsAllVotersAbstainGrantAccess() {
		boolean expResult = false;
		boolean result = bipOpaProperties.isAllVotersAbstainGrantAccess();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setAllVotersAbstainGrantAccess method, of class BipOpaProperties.
	 */
	@Test
	public void testSetAllVotersAbstainGrantAccess() {
		boolean enabled = true;
		bipOpaProperties.setAllVotersAbstainGrantAccess(enabled);
		assertTrue(bipOpaProperties.isAllVotersAbstainGrantAccess());
	}

	/**
	 * Test of getUrls method, of class BipOpaProperties.
	 */
	@Test
	public void testGetUrls() {
		assertTrue(bipOpaProperties.getUrls().length == 0);
	}

	/**
	 * Test of setUrls method, of class BipOpaProperties.
	 */
	@Test
	public void testSetUrls() {
		bipOpaProperties.setUrls(new String[] { "http://localhost:8181/api/v1/mytest/pid" });
		assertNotNull(bipOpaProperties.getUrls());
	}
}
