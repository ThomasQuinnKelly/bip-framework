package gov.va.bip.framework.s3.config;

import gov.va.bip.framework.sns.config.SnsProperties;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class S3PropertiesTest {

    //Test S3 Logger
    @Test
    public void testLogger() {
        Logger logger = LoggerFactory.getLogger(SnsProperties.class);
        S3Properties instance = new S3Properties();
        instance.setLogger(logger);

        assertEquals(Optional.of(instance.getLogger()), Optional.ofNullable(logger));
    }

    //Test S3 Property enabled
    @Test
    public void testBoolean() {
        boolean enabled = true;
        S3Properties instance = new S3Properties();
        instance.setEnabled(enabled);

        assertEquals(Optional.of(instance.getEnabled()), Optional.ofNullable(enabled));
    }

    //Test S3 Property name
    @Test
    public void testBuckets() {
        List<S3Properties.Bucket> buckets = new ArrayList<>();
        S3Properties instance = new S3Properties();
        instance.setBuckets(buckets);

        assertEquals(Optional.of(instance.getBuckets()), Optional.ofNullable(buckets));
    }

    //Test S3 Property type
    @Test
    public void testBucketEndpoint() {
        String endpoint = "url:port/bucket/";
        String name = "String";
        S3Properties.Bucket instance = new S3Properties.Bucket();
        instance.setEndpoint(endpoint + name);

        assertEquals(Optional.of(instance.getEndpoint()), Optional.ofNullable(endpoint + name));
        assertEquals(Optional.of(instance.getName()), Optional.ofNullable(name));
    }

    //Test S3 Property region
    @Test
    public void testRegion() {
        String region = "us-east-1";
        S3Properties instance = new S3Properties();
        instance.setRegion(region);

        assertEquals(Optional.of(instance.getRegion()), Optional.ofNullable(region));
    }

    //Test S3 Property endpoint
    @Test
    public void testEndpoint() {
        String endpoint = "http://localhost:4575/blah";
        S3Properties instance = new S3Properties();
        instance.setEndpoint(endpoint);

        assertEquals(Optional.of(instance.getEndpoint()), Optional.ofNullable(endpoint));
    }

    //Test S3 BaseUrl
    @Test
    public void testGetBaseUrl() {
        String endpoint = "http://localhost:4575";
        S3Properties instance = new S3Properties();
        instance.setEndpoint(endpoint+"/blah");

        assertEquals(Optional.of(instance.getBaseUrl()), Optional.ofNullable(endpoint));
    }
}