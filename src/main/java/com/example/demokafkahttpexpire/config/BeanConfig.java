package com.example.demokafkahttpexpire.config;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class BeanConfig {

	public static final String TOPIC_REQ = "http-req";

	public static final String TOPIC_RES = "http-res";

	public static final String REQUEST_ID = "req-id";

	public static final String CORRELATION_ID = "correlation-id";

	@Bean
	public NewTopic reqTopic() {
		return TopicBuilder.name(TOPIC_REQ).build();
	}

	@Bean
	public NewTopic resTopic() {
		return TopicBuilder.name(TOPIC_RES).build();
	}

	@Bean
	public ConcurrentMap<String, BlockingDeque<String>> responseMap() {
		return new ConcurrentHashMap<>();
	}

}
