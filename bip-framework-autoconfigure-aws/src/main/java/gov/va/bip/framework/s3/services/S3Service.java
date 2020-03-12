package gov.va.bip.framework.s3.services;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.File;

public interface S3Service{

    PutObjectResult uploadFile(String bucketName, String fileName, File file);

    PutObjectResult uploadFile(PutObjectRequest putObjectRequest);

    void deleteObject(String bucketName, String objectName);

    void deleteObject(DeleteObjectRequest deleteObjectRequest);
}