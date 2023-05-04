package com.example.demokafkahttpexpire.listener;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.example.demokafkahttpexpire.config.BeanConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseListener {

	private final ConcurrentMap<String, BlockingDeque<String>> responseMap;

	@KafkaListener(id = "res", topics = BeanConfig.TOPIC_RES)
	public void onMessage(ConsumerRecord<String, String> record,
			@Header(BeanConfig.CORRELATION_ID) String correlationId) {
		keepResponse(record.value(), correlationId);
	}

	private void keepResponse(String value, String correlationId) {
		log.info("correlation-id: {}", correlationId);
		try {
			responseMap.get(correlationId).offer(value, 1, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("[REV RES] correlation-id: {}, value: {}", correlationId, value);
	}

}
