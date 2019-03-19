package gov.va.ocp.framework.test.restassured;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.ocp.framework.test.util.RESTUtil;
import io.restassured.response.Response;

@RunWith(MockitoJUnitRunner.class)
public class BaseStepDefTest {

	BaseStepDef subject = new BaseStepDef();

	@Mock
	Response response;

	@Before
	public void setup() {
		Map<String, String> tblHeader = new HashMap<>();
		tblHeader.put("Acept", "application/json");
		tblHeader.put("Content-Type", "application/json");
		subject.passHeaderInformation(tblHeader);	
		subject.initREST();
	}
	
	@Test
	public void test_passHeaderInformation_Success() {
		assertThat(2, equalTo(subject.headerMap.size()));
	}
	
	@Test
	public void test_invokeAPIUsingDelete_Success() {
		RESTUtil restUtil = new RESTUtil();
		RESTUtil spyRestUtil = Mockito.spy(restUtil);
		Mockito.doNothing().when(spyRestUtil).doWithRetry(anyObject(), anyInt());
		ReflectionTestUtils.setField(spyRestUtil, "response", response);
		ReflectionTestUtils.setField(subject, "resUtil", spyRestUtil);
		subject.invokeAPIUsingDelete("http://localhost/testUrl", false);
	}
	
	@Test
	public void test_invokeAPIUsingPost_Success() {
		RESTUtil restUtil = new RESTUtil();
		RESTUtil spyRestUtil = Mockito.spy(restUtil);
		Mockito.doNothing().when(spyRestUtil).doWithRetry(anyObject(), anyInt());
		ReflectionTestUtils.setField(spyRestUtil, "response", response);
		ReflectionTestUtils.setField(subject, "resUtil", spyRestUtil);
		subject.invokeAPIUsingPost("http://localhost/testUrl", false);
	}
	
	@Test
	public void test_invokeAPIUsingGet_Success() {
		RESTUtil restUtil = new RESTUtil();
		RESTUtil spyRestUtil = Mockito.spy(restUtil);
		Mockito.doNothing().when(spyRestUtil).doWithRetry(anyObject(), anyInt());
		ReflectionTestUtils.setField(spyRestUtil, "response", response);
		ReflectionTestUtils.setField(subject, "resUtil", spyRestUtil);
		subject.invokeAPIUsingGet("http://localhost/testUrl", false);
	}
	
	@Test
	public void test_setHeader_Success() throws Exception {
		subject.setHeader("dev-janedoe");
		assertThat(2, equalTo(subject.headerMap.size()));
	}
	
	@Test
	public void test_compareExpectedResponseWithActual_Success() throws Exception {	
		final URL urlFilePath = BaseStepDefTest.class.getClassLoader().getResource("response/test.response");
		final File strFilePath = new File(urlFilePath.toURI());
		subject.strResponse = FileUtils.readFileToString(strFilePath, "ASCII");
		assertThat(true, equalTo(subject.compareExpectedResponseWithActual("test.response")));
	}
	
	@Test
	public void test_compareExpectedResponseWithActualByRow_Success() throws Exception {
		final URL urlFilePath = BaseStepDefTest.class.getClassLoader().getResource("response/test.response");
		final File strFilePath = new File(urlFilePath.toURI());
		subject.strResponse = FileUtils.readFileToString(strFilePath, "ASCII");
		assertThat(true, equalTo(subject.compareExpectedResponseWithActual("test.response")));
	}
		
	@Test
	public void test_SetEnvQA_readProperty_Success() {
		System.setProperty("test.env", "qa");
	}
	

}
