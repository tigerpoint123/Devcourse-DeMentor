package com.dementor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;

public abstract class RedisTestContainerConfig {
	static final GenericContainer<?> redisContainer =
		new GenericContainer<>("redis:7.0.12")
			.withExposedPorts(6379)
			.withReuse(true);

	@BeforeAll
	static void setUp() {
		redisContainer.start();
		System.setProperty("spring.redis.host", redisContainer.getHost());
		System.setProperty("spring.redis.port", redisContainer.getMappedPort(6379).toString());
	}

	@AfterAll
	static void tearDown() {
		redisContainer.stop();
	}
}
