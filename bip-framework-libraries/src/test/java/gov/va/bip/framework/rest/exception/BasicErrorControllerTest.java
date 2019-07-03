package gov.va.bip.framework.rest.exception;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class BasicErrorControllerTest {

	private MockMvc mockMvc;

	private BasicErrorController basicErrorController = new BasicErrorController();

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(basicErrorController).build();
	}

	@Test
	public void testBasicErrorController() throws Exception{

		this.mockMvc.perform(post("/error")
				.accept(MediaType.parseMediaType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
	}
	
	@Test
	public void testBasicErrorControllerPath() throws Exception{

		String errorPath = basicErrorController.getErrorPath();
		assertSame("/error", errorPath);
	}

	@Test
	public void testBasicErrorControllerWithMessage() throws Exception{

		ReflectionTestUtils.setField(basicErrorController, "errorAttributes", mock(ErrorAttributes.class));
		this.mockMvc.perform(post("/error")
				.with(new RequestPostProcessor() { 
					@Override
					public MockHttpServletRequest postProcessRequest(final MockHttpServletRequest request) {
						request.setAttribute("message", "Unexpected Error Occured");
						return request;
					}})
				.accept(MediaType.parseMediaType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
	}
	
}
