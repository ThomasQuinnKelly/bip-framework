package gov.va.ocp.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URL;
import java.util.Properties;

import org.junit.Test;

import gov.va.ocp.framework.test.util.PropertiesUtil;

public class PropertiesUtilTest {


	@Test
	public void testreadFile_Success() {
		
		final URL urlConfigFile = PropertiesUtilTest.class.getClassLoader().getResource("test-properties.properties");
		Properties properties = PropertiesUtil.readFile(urlConfigFile);
		assertThat("reference", equalTo(properties.get("project")));
	}
  public void testreadEmptyFile_Success() {
		
		final URL urlConfigFile = PropertiesUtilTest.class.getClassLoader().getResource("empty-properties.properties");
		Properties properties = PropertiesUtil.readFile(urlConfigFile);
		assertThat(true, equalTo(properties.isEmpty()));
	}


	@Test (expected = NullPointerException.class)
	public void testreadFile_failure() {
		
		final URL urlConfigFile = PropertiesUtilTest.class.getClassLoader().getResource("test-not-exists.properties");
		PropertiesUtil.readFile(urlConfigFile);
	}
}
