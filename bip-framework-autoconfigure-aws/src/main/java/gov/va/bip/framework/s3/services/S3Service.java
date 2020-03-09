package gov.va.bip.framework.s3.services;

import com.amazonaws.services.s3.model.*;

import java.io.File;

public interface S3Service{

    Bucket createBucket(String bucketName);

    Bucket createBucket(CreateBucketRequest createBucketRequest);

    PutObjectResult uploadFile(String bucketName, String fileName, File file);

    PutObjectResult uploadFile(PutObjectRequest putObjectRequest);

    void deleteObject(String bucketName, String objectName);

    void deleteObject(DeleteObjectRequest deleteObjectRequest);
}