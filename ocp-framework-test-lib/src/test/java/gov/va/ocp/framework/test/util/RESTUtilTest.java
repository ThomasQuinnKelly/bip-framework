package gov.va.ocp.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.restassured.response.Response;

@RunWith(MockitoJUnitRunner.class)
public class RESTUtilTest {

	@Mock
	Response response;

	@Test
	public void test_setUpRequest_Success() {

		RESTUtil restUtil = new RESTUtil();
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put("authorization", "Bearer abcdef");
		mapHeader.put("content-type", "application/json");
		restUtil.setUpRequest(mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get("authorization")));
	}

	@Test
	public void test_setUpRequest_WithBody_Success() {

		RESTUtil restUtil = new RESTUtil();
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put("authorization", "Bearer abcdef");
		mapHeader.put("content-type", "application/json");
		restUtil.setUpRequest("janedoe.request", mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get("authorization")));
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_setUpRequest_WithBody_Failed() {

		RESTUtil restUtil = new RESTUtil();
		Map<String, String> mapHeader = new HashMap<String, String>();
		restUtil.setUpRequest("nonexistsfile.request", mapHeader);
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(true, equalTo(isBodyEmpty));
	}

	@Test
	public void test_getResponse_Success() {
		RESTUtil restUtil = new RESTUtil();
		RESTUtil spyRestUtil = Mockito.spy(restUtil);
		Mockito.doNothing().when(spyRestUtil).doWithRetry(anyObject(), anyInt());		
		ReflectionTestUtils.setField(spyRestUtil, "response", response);
		spyRestUtil.getResponse("http://localhost:8080/somepath");
	}
	@Test
	public void test_postResponse_Success() {
		RESTUtil restUtil = new RESTUtil();
		RESTUtil spyRestUtil = Mockito.spy(restUtil);
		Mockito.doNothing().when(spyRestUtil).doWithRetry(anyObject(), anyInt());
		ReflectionTestUtils.setField(spyRestUtil, "response", response);
		spyRestUtil.postResponse("http://localhost:8080/somepath");
	}

	@Test
	public void test_deleteResponse_Success() {
		RESTUtil restUtil = new RESTUtil();
		RESTUtil spyRestUtil = Mockito.spy(restUtil);
		Mockito.doNothing().when(spyRestUtil).doWithRetry(anyObject(), anyInt());
		ReflectionTestUtils.setField(spyRestUtil, "response", response);
		spyRestUtil.deleteResponse("http://localhost:8080/somepath");
	}
	@Test
	public void test_putResponse_Success() {
		RESTUtil restUtil = new RESTUtil();
		RESTUtil spyRestUtil = Mockito.spy(restUtil);
		Mockito.doNothing().when(spyRestUtil).doWithRetry(anyObject(), anyInt());
		ReflectionTestUtils.setField(spyRestUtil, "response", response);
		spyRestUtil.putResponse("http://localhost:8080/somepath");
	}




	@Test
	public void test_readExpectedResponse_Success() {
		RESTUtil restUtil = new RESTUtil();
		String response = restUtil.readExpectedResponse("test.response");
		boolean isBodyEmpty = response.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_readExpectedResponse_FileNotExists_Success() {
		RESTUtil restUtil = new RESTUtil();
		String response = restUtil.readExpectedResponse("nonexistsfile.response");
		assertThat(null, equalTo(response));
	}

}
