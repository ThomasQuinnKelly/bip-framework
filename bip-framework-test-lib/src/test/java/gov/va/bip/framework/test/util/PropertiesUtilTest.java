package gov.va.bip.framework.test.util;

import gov.va.bip.framework.test.exception.BipTestLibRuntimeException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class PropertiesUtilTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test(expected = UnsupportedOperationException.class)
	public void utilityClassTest() throws NoSuchMethodException, IllegalAccessException, InstantiationException {
		final Constructor<PropertiesUtil> constructor = PropertiesUtil.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
		} catch (InvocationTargetException e) {
			throw (UnsupportedOperationException) e.getTargetException();
		}
	}

	@Test
	public void testreadFile_Success() {
		final URL urlConfigFile = PropertiesUtilTest.class.getClassLoader().getResource("test-properties.properties");
		Properties properties = null;
		try {
			properties = PropertiesUtil.readFile(urlConfigFile);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat("reference", equalTo(properties.get("project")));
	}

	@Test(expected = BipTestLibRuntimeException.class)
	public void testreadFile_badcorrupted_Success() throws URISyntaxException {
		final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("BAD_corrupted-pdf.pdf");
		File strFilePath;
		strFilePath = new File(urlFilePath.toURI());
		strFilePath.setReadable(false);
		final URL urlConfigFile = PropertiesUtilTest.class.getClassLoader().getResource("BAD_corrupted-pdf.pdf");
		try {
			PropertiesUtil.readFile(urlConfigFile);
		} catch (BipTestLibRuntimeException e) {
			strFilePath.setReadable(true);
			e.printStackTrace();
			throw e;
		}

	}

	@Test
	public void testreadEmptyFile_Success() {

		final URL urlConfigFile = PropertiesUtilTest.class.getClassLoader().getResource("empty-properties.properties");
		Properties properties = null;
		try {
			properties = PropertiesUtil.readFile(urlConfigFile);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(true, equalTo(properties.isEmpty()));
	}

	@Test(expected = BipTestLibRuntimeException.class)
	public void testreadFile_failure() throws URISyntaxException, IOException, MalformedURLException {
		Properties properties = null;
		properties = PropertiesUtil.readFile(new URL("file:/E@@:/Program Files/IBM/SDP/runtimes/base"));
		assertThat(true, equalTo(properties == null));
	}
	
	@Test
	public void testreadFileNullUrl() {

		Properties properties = null;
		try {
			properties = PropertiesUtil.readFile(null);
		} catch (BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
		assertThat(true, equalTo(properties == null));
	}
	
	@Test(expected = BipTestLibRuntimeException.class)
	public void testreadFile_IllegalArgumentException() {
		final URL urlConfigFile = PropertiesUtilTest.class.getClassLoader().getResource("badproperties.properties");
		Properties properties = PropertiesUtil.readFile(urlConfigFile);
		assertThat(true, equalTo(properties == null));
	}


}
