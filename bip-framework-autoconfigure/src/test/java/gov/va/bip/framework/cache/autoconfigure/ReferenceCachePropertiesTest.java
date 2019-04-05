package gov.va.bip.framework.cache.autoconfigure;

import org.junit.Test;

import gov.va.bip.framework.cache.autoconfigure.BipCacheProperties;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReferenceCachePropertiesTest {

	@Test
	public void testGetters() {
		BipCacheProperties bipCacheProperties = new BipCacheProperties();
		assertNull(bipCacheProperties.getExpires());
		assertEquals(new Long(86400L), bipCacheProperties.getDefaultExpires());
	}

	@Test
	public void testSetters() {
		BipCacheProperties bipCacheProperties = new BipCacheProperties();
		List<BipCacheProperties.RedisExpires> listRedisExpires = new ArrayList<>();
		BipCacheProperties.RedisExpires redisExpires = new BipCacheProperties.RedisExpires();
		redisExpires.setName("methodcachename_projectname_projectversion");
		redisExpires.setTtl(86400L);
		listRedisExpires.add(0, redisExpires);
		bipCacheProperties.setExpires(listRedisExpires);
		bipCacheProperties.setDefaultExpires(500L);
		assertTrue(!bipCacheProperties.getExpires().isEmpty());
		assertTrue(Long.valueOf(86400L).equals(bipCacheProperties.getExpires().get(0).getTtl()));
		assertEquals(new Long(500L), bipCacheProperties.getDefaultExpires());
	}
}