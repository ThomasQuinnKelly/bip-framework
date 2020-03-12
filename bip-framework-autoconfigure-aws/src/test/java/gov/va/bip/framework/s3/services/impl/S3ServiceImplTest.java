package gov.va.bip.framework.s3.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import gov.va.bip.framework.aws.autoconfigure.BipS3AutoConfiguration;
import gov.va.bip.framework.exception.S3Exception;
import gov.va.bip.framework.messages.MessageKeys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@Import(BipS3AutoConfiguration.class)
public class S3ServiceImplTest {

	@Autowired
	S3ServiceImpl instance;

	@MockBean
	AmazonS3 amazonS3;

	@Mock
	PutObjectResult mockPutObjectResult;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test_uploadFile_1_Success() {
		//Setup
		when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(mockPutObjectResult);

		//Test
		PutObjectResult result = instance.uploadFile("a", "b", new File("c"));

		//Verify
		assertEquals(mockPutObjectResult, result);
	}

	@Test
	public void test_uploadFile_1_Exception1() {
		//Setup

		//Test
		try {
			instance.uploadFile(null, "b", new File("c"));
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_UPLOAD_FILE_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}

	@Test
	public void test_uploadFile_1_Exception2() {
		//Setup

		//Test
		try {
			instance.uploadFile("a", null, new File("c"));
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_UPLOAD_FILE_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}

	@Test
	public void test_uploadFile_1_Exception3() {
		//Setup

		//Test
		try {
			instance.uploadFile("a", "b", null);
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_UPLOAD_FILE_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}

	@Test
	public void test_uploadFile_2_Success() {
		//Setup
		when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(mockPutObjectResult);
		PutObjectRequest putObjectRequest = new PutObjectRequest("a", "b", new File("c"));

		//Test
		PutObjectResult result = instance.uploadFile(putObjectRequest);

		//Verify
		assertEquals(mockPutObjectResult, result);
	}

	@Test
	public void test_uploadFile_2_Exception1() {
		//Setup
		PutObjectRequest putObjectRequest = new PutObjectRequest(null, "b", new File("c"));

		//Test
		try {
			instance.uploadFile(putObjectRequest);
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_UPLOAD_FILE_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}

	@Test
	public void test_uploadFile_2_Exception2() {
		//Setup
		PutObjectRequest putObjectRequest = new PutObjectRequest("a", null, new File("c"));

		//Test
		try {
			instance.uploadFile(putObjectRequest);
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_UPLOAD_FILE_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}

	@Test
	public void test_uploadFile_2_Exception3() {
		//Setup
		File nullFile = null;
		PutObjectRequest putObjectRequest = new PutObjectRequest("a", "b", nullFile);

		//Test
		try {
			instance.uploadFile(putObjectRequest);
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_UPLOAD_FILE_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}

	@Test
	public void test_deleteObject_1_Success() {
		//Setup
		doNothing().when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

		//Test
		instance.deleteObject("a", "b");

		//Verify
		verify(amazonS3, times(1)).deleteObject(any(DeleteObjectRequest.class));
	}

	@Test
	public void test_deleteObject_1_Exception1() {
		//Setup

		//Test
		try {
			instance.deleteObject(null, "b");
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_DELETE_OBJECT_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}

	@Test
	public void test_deleteObject_1_Exception2() {
		//Setup

		//Test
		try {
			instance.deleteObject("a", null);
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_DELETE_OBJECT_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}

	@Test
	public void test_deleteObject_2_Success() {
		//Setup
		doNothing().when(amazonS3).deleteObject(any(DeleteObjectRequest.class));
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest("a", "b");

		//Test
		instance.deleteObject(deleteObjectRequest);

		//Verify
		verify(amazonS3, times(1)).deleteObject(any(DeleteObjectRequest.class));
	}

	@Test
	public void test_deleteObject_2_Exception1() {
		//Setup
		doNothing().when(amazonS3).deleteObject(any(DeleteObjectRequest.class));
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(null, "b");

		//Test
		try {
			instance.deleteObject(deleteObjectRequest);
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_DELETE_OBJECT_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}

	@Test
	public void test_deleteObject_2_Exception2() {
		//Setup
		doNothing().when(amazonS3).deleteObject(any(DeleteObjectRequest.class));
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest("a", null);

		//Test
		try {
			instance.deleteObject(deleteObjectRequest);
			//Verify
			fail("An exception should have been thrown here!");
		} catch(S3Exception e) {
			assertEquals(MessageKeys.BIP_S3_DELETE_OBJECT_EXCEPTION_MESSAGE, e.getExceptionData().getMessageKey());
		}
	}
}
