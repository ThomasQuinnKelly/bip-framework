package gov.va.bip.framework.cache.autoconfigure;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BipRedisCachePropertiesTest {

	@Test
	public void testGetters() {
		BipRedisCacheProperties bipRedisCacheProperties = new BipRedisCacheProperties();
		assertNull(bipRedisCacheProperties.getExpires());
		assertEquals(new Long(86400L), bipRedisCacheProperties.getDefaultExpires());
	}

	@Test
	public void testSetters() {
		BipRedisCacheProperties bipRedisCacheProperties = new BipRedisCacheProperties();
		List<BipRedisCacheProperties.RedisExpires> listRedisExpires = new ArrayList<>();
		BipRedisCacheProperties.RedisExpires redisExpires = new BipRedisCacheProperties.RedisExpires();
		redisExpires.setName("methodcachename_projectname_projectversion");
		redisExpires.setTtl(86400L);
		listRedisExpires.add(0, redisExpires);
		bipRedisCacheProperties.setExpires(listRedisExpires);
		bipRedisCacheProperties.setDefaultExpires(500L);
		assertTrue(!bipRedisCacheProperties.getExpires().isEmpty());
		assertTrue(Long.valueOf(86400L).equals(bipRedisCacheProperties.getExpires().get(0).getTtl()));
		assertEquals(new Long(500L), bipRedisCacheProperties.getDefaultExpires());
	}
}