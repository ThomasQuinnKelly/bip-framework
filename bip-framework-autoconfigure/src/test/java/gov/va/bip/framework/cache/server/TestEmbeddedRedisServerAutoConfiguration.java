package gov.va.bip.framework.cache.server;

import gov.va.bip.framework.cache.autoconfigure.BipRedisCacheProperties;
import gov.va.bip.framework.cache.autoconfigure.server.BipEmbeddedRedisServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author rthota
 *
 */
@Configuration
public class TestEmbeddedRedisServerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public BipEmbeddedRedisServer bipEmbeddedRedisServer() {
		return new BipEmbeddedRedisServer();
	}

	@Bean
	@ConditionalOnMissingBean
	public RedisProperties redisProperties() {
		RedisProperties redisProperties = new RedisProperties();
		redisProperties.setPort(0);
		return redisProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public BipRedisCacheProperties bipRedisCacheProperties() {
		return new BipRedisCacheProperties();
	}
}