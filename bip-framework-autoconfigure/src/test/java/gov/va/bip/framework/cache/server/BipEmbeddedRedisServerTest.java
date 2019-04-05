package gov.va.bip.framework.cache.server;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.bip.framework.cache.autoconfigure.server.BipEmbeddedRedisServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * @author rthota
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ReferenceEmbeddedRedisServerAutoConfiguration.class)
public class BipEmbeddedRedisServerTest {

	@Autowired
	BipEmbeddedRedisServer referenceEmbeddedServer;

	@Before
	public void setUp() {
		if (referenceEmbeddedServer.getRedisServer().isActive()) {
			referenceEmbeddedServer.stopRedis();
		}
	}

	@Test(timeout = 1500L)
	public void testSimpleRun() throws Exception {
		referenceEmbeddedServer.startRedis();
		referenceEmbeddedServer.stopRedis();
	}


	@Test
	public void shouldAllowSubsequentRuns() throws Exception {
		referenceEmbeddedServer.startRedis();
		referenceEmbeddedServer.stopRedis();

		referenceEmbeddedServer.startRedis();
		referenceEmbeddedServer.stopRedis();

		referenceEmbeddedServer.startRedis();
		referenceEmbeddedServer.stopRedis();
	}

	@Test
	public void testSimpleOperationsAfterRun() throws Exception {
		referenceEmbeddedServer.startRedis();

		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = new JedisPool("localhost", referenceEmbeddedServer.getRedisServer().ports().get(0));
			jedis = pool.getResource();
			jedis.mset("abc", "1", "def", "2");

			assertEquals("1", jedis.mget("abc").get(0));
			assertEquals("2", jedis.mget("def").get(0));
			assertEquals(null, jedis.mget("xyz").get(0));
		} finally {
			if (jedis != null)
				pool.close();
			referenceEmbeddedServer.stopRedis();
		}
	}

	@Test
	public void shouldIndicateInactiveBeforeStart() throws Exception {
		assertFalse(referenceEmbeddedServer.getRedisServer().isActive());
	}

	@Test
	public void shouldIndicateActiveAfterStart() throws Exception {
		referenceEmbeddedServer.startRedis();
		assertTrue(referenceEmbeddedServer.getRedisServer().isActive());
		referenceEmbeddedServer.stopRedis();
	}

	@Test
	public void shouldIndicateInactiveAfterStop() throws Exception {
		referenceEmbeddedServer.startRedis();
		referenceEmbeddedServer.stopRedis();
		assertFalse(referenceEmbeddedServer.getRedisServer().isActive());
	}

	@After
	public void teardown() {
		if (referenceEmbeddedServer.getRedisServer().isActive()) {
			referenceEmbeddedServer.stopRedis();
		}
	}
}