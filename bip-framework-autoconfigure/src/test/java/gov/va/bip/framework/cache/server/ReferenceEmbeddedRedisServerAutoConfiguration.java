package gov.va.bip.framework.cache.server;

import java.util.ArrayList;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.bip.framework.cache.autoconfigure.BipCacheProperties;
import gov.va.bip.framework.cache.autoconfigure.BipCacheProperties.RedisConfig;
import gov.va.bip.framework.cache.autoconfigure.server.BipEmbeddedRedisServer;

/**
 * 
 * @author rthota
 *
 */
@Configuration
public class ReferenceEmbeddedRedisServerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public BipEmbeddedRedisServer bipEmbeddedRedisServer() {
		return new BipEmbeddedRedisServer();
	}

	@Bean
	@ConditionalOnMissingBean
	public BipCacheProperties bipCacheProperties() {
		BipCacheProperties bipCacheProperties = new BipCacheProperties();
		bipCacheProperties.setRedisConfig(new RedisConfig());
		bipCacheProperties.getRedisConfig().setHost("localhost");
		bipCacheProperties.setExpires(new ArrayList<>());
		bipCacheProperties.setDefaultExpires(500L);
		return bipCacheProperties;
	}
}