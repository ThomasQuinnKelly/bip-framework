package gov.va.ocp.framework.cache.autoconfigure;

import org.junit.Test;

import gov.va.ocp.framework.cache.autoconfigure.OcpCacheProperties;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReferenceCachePropertiesTest {

	@Test
	public void testGetters() {
		OcpCacheProperties ocpCacheProperties = new OcpCacheProperties();
		assertNull(ocpCacheProperties.getExpires());
		assertEquals(new Long(86400L), ocpCacheProperties.getDefaultExpires());
	}

	@Test
	public void testSetters() {
		OcpCacheProperties ocpCacheProperties = new OcpCacheProperties();
		List<OcpCacheProperties.RedisExpires> listRedisExpires = new ArrayList<>();
		OcpCacheProperties.RedisExpires redisExpires = new OcpCacheProperties.RedisExpires();
		redisExpires.setName("methodcachename_projectname_projectversion");
		redisExpires.setTtl(86400L);
		listRedisExpires.add(0, redisExpires);
		ocpCacheProperties.setExpires(listRedisExpires);
		ocpCacheProperties.setDefaultExpires(500L);
		assertTrue(!ocpCacheProperties.getExpires().isEmpty());
		assertTrue(Long.valueOf(86400L).equals(ocpCacheProperties.getExpires().get(0).getTtl()));
		assertEquals(new Long(500L), ocpCacheProperties.getDefaultExpires());
	}
}