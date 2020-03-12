package gov.va.bip.framework.s3.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import gov.va.bip.framework.exception.S3Exception;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.s3.config.S3Properties;
import gov.va.bip.framework.s3.services.S3Service;
import gov.va.bip.framework.validation.Defense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class S3ServiceImpl implements S3Service {

	private final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);
	public static final String ERROR_MESSAGE = "Error Message: {}";

	@Autowired
	S3Properties s3Properties;

	@Autowired
	AmazonS3 amazonS3;

	@Override
	public PutObjectResult uploadFile(String bucketName, String fileName, File file) {

		try {
			Defense.notNull(bucketName, "bucketName can't be null.");
			Defense.notNull(fileName, "fileName can't be null.");
			Defense.notNull(file, "file can't be null.");

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new S3Exception(MessageKeys.BIP_S3_UPLOAD_FILE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
		}

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);

		return amazonS3.putObject(putObjectRequest);
	}

	@Override
	public PutObjectResult uploadFile(PutObjectRequest putObjectRequest) {

		try {
			Defense.notNull(putObjectRequest, "PutObjectRequest can't be null.");
			Defense.notNull(putObjectRequest.getBucketName(), "PutObjectRequest.getBucketName() can't be null.");
			Defense.notNull(putObjectRequest.getKey(), "PutObjectRequest.getKey() can't be null.");
			Defense.notNull(putObjectRequest.getFile(), "PutObjectRequest.getFile() can't be null.");

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new S3Exception(MessageKeys.BIP_S3_UPLOAD_FILE_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
		}

		return amazonS3.putObject(putObjectRequest);
	}

	@Override
	public void deleteObject(String bucketName, String objectName) {

		try {
			Defense.notNull(bucketName, "bucketName can't be null.");
			Defense.notNull(objectName, "objectName can't be null.");

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new S3Exception(MessageKeys.BIP_S3_DELETE_OBJECT_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
		}

		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName + "/", objectName);

		amazonS3.deleteObject(deleteObjectRequest);
	}

	@Override
	public void deleteObject(DeleteObjectRequest deleteObjectRequest) {

		try {
			Defense.notNull(deleteObjectRequest, "DeleteObjectRequest can't be null.");
			Defense.notNull(deleteObjectRequest.getBucketName(), "DeleteObjectRequest.getBucketName() can't be null.");
			Defense.notNull(deleteObjectRequest.getKey(), "DeleteObjectRequest.getKey() can't be null.");

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new S3Exception(MessageKeys.BIP_S3_DELETE_OBJECT_EXCEPTION_MESSAGE, MessageSeverity.ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage());
		}

		amazonS3.deleteObject(deleteObjectRequest);
	}
}





