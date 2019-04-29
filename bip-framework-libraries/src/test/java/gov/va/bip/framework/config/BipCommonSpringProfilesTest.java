package gov.va.bip.framework.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import gov.va.bip.framework.config.BipCommonSpringProfiles;

public class BipCommonSpringProfilesTest {

	/**
	 * Spring default profile
	 */
	public static final String TEST_PROFILE_DEFAULT = "default";

	/**
	 * Spring profile for local dev environment
	 */
	public static final String TEST_PROFILE_ENV_LOCAL_INT = "local-int";

	/**
	 * Spring profile for local dev environment
	 */
	public static final String TEST_PROFILE_ENV_DOCKER_DEMO = "docker-demo";

	/**
	 * Spring profile for AWS CI environment
	 */
	public static final String TEST_PROFILE_ENV_CI = "ci";

	/**
	 * Spring profile for AWS DEV environment
	 */
	public static final String TEST_PROFILE_ENV_DEV = "dev";

	/**
	 * Spring profile for AWS STAGE environment
	 */
	public static final String TEST_PROFILE_ENV_STAGE = "stage";

	/**
	 * Spring profile for AWS PROD environment
	 */
	public static final String TEST_PROFILE_ENV_PROD = "prod";

	/**
	 * Spring profile for remote client real implementations
	 */
	public static final String TEST_PROFILE_REMOTE_CLIENT_IMPLS = "remote_client_impls";

	/**
	 * Spring profile for remote client simulator implementations
	 */
	public static final String TEST_PROFILE_REMOTE_CLIENT_SIMULATORS = "remote_client_sims";

	/**
	 * Spring profile for unit test specific impls
	 */
	public static final String TEST_PROFILE_UNIT_TEST = "unit_test_sims";

	/**
	 * Spring profile for remote audit simulator implementations
	 */
	public static final String TEST_PROFILE_REMOTE_AUDIT_SIMULATORS = "remote_audit_client_sims";

	/**
	 * Spring profile for remote audit impl implementations
	 */
	public static final String TEST_PROFILE_REMOTE_AUDIT_IMPLS = "remote_audit_client_impl";

	/**
	 * Spring Profile to signify that the application will run embedded redis
	 */
	public static final String TEST_PROFILE_EMBEDDED_REDIS = "embedded-redis";

	/**
	 * Spring Profile to signify that the application will run embedded AWS
	 */
	public static final String TEST_PROFILE_EMBEDDED_AWS = "embedded-aws";

	/**
	 * Spring Profile to signify that the configuration will not be loaded in embedded aws
	 */
	public static final String TES_NOT_PROFILE_EMBEDDED_AWS = "!embedded-aws";

	@Test
	public void profileDefaultTest() throws Exception {
		assertEquals(TEST_PROFILE_DEFAULT, BipCommonSpringProfiles.PROFILE_DEFAULT);
	}

	@Test
	public void profileLocalIntTest() throws Exception {
		assertEquals(TEST_PROFILE_ENV_LOCAL_INT, BipCommonSpringProfiles.PROFILE_ENV_LOCAL_INT);
	}

	@Test
	public void profileDockerDemoTest() throws Exception {
		assertEquals(TEST_PROFILE_ENV_DOCKER_DEMO, BipCommonSpringProfiles.PROFILE_ENV_DOCKER_DEMO);
	}

	@Test
	public void profileAwsCITest() throws Exception {
		assertEquals(TEST_PROFILE_ENV_CI, BipCommonSpringProfiles.PROFILE_ENV_CI);
	}

	@Test
	public void profileAwsDevTest() throws Exception {
		assertEquals(TEST_PROFILE_ENV_DEV, BipCommonSpringProfiles.PROFILE_ENV_DEV);
	}

	@Test
	public void profileAwsStageTest() throws Exception {
		assertEquals(TEST_PROFILE_ENV_STAGE, BipCommonSpringProfiles.PROFILE_ENV_STAGE);
	}

	@Test
	public void profileAwsProdTest() throws Exception {
		assertEquals(TEST_PROFILE_ENV_PROD, BipCommonSpringProfiles.PROFILE_ENV_PROD);
	}

	@Test
	public void profileRemoteClientSimulatorsTest() throws Exception {
		assertEquals(TEST_PROFILE_REMOTE_CLIENT_SIMULATORS, BipCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS);
	}

	@Test
	public void profileRemoteClientImplsTest() throws Exception {
		assertEquals(TEST_PROFILE_REMOTE_CLIENT_IMPLS, BipCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS);
	}

	@Test
	public void profileRemoteAuditSimulatorsTest() throws Exception {
		assertEquals(TEST_PROFILE_REMOTE_AUDIT_SIMULATORS, BipCommonSpringProfiles.PROFILE_REMOTE_AUDIT_SIMULATORS);
	}

	@Test
	public void profileRemoteAuditImplsTest() throws Exception {
		assertEquals(TEST_PROFILE_REMOTE_AUDIT_IMPLS, BipCommonSpringProfiles.PROFILE_REMOTE_AUDIT_IMPLS);
	}

	@Test
	public void profileUnitTestingTest() throws Exception {
		assertEquals(TEST_PROFILE_UNIT_TEST, BipCommonSpringProfiles.PROFILE_UNIT_TEST);
	}

	@Test
	public void profileEmbeddedRedisTest() throws Exception {
		assertEquals(TEST_PROFILE_EMBEDDED_REDIS, BipCommonSpringProfiles.PROFILE_EMBEDDED_REDIS);
	}

	@Test
	public void profileEmbeddedAwsTest() throws Exception {
		assertEquals(TEST_PROFILE_EMBEDDED_AWS, BipCommonSpringProfiles.PROFILE_EMBEDDED_AWS);
	}

	@Test
	public void notProfileEmbeddedAwsTest() throws Exception {
		assertEquals(TES_NOT_PROFILE_EMBEDDED_AWS, BipCommonSpringProfiles.NOT_PROFILE_EMBEDDED_AWS);
	}

	@Test
	public void referenceCommonSpringProfilesConstructor() throws Exception {
		Constructor<BipCommonSpringProfiles> c = BipCommonSpringProfiles.class.getDeclaredConstructor((Class<?>[]) null);
		c.setAccessible(true);
		try {
			c.newInstance();
			fail("Should have thrown exception");
		} catch (Exception e) {
			assertTrue(InvocationTargetException.class.equals(e.getClass()));
			assertTrue(IllegalStateException.class.equals(e.getCause().getClass()));
		}
	}
}
