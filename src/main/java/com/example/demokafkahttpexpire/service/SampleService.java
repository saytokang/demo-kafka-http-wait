
package com.example.demokafkahttpexpire.service;

import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.demokafkahttpexpire.config.BeanConfig;
import com.example.demokafkahttpexpire.listener.RequestListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleService {

	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ConcurrentMap<String, BlockingDeque<String>> responseMap;

	private final ScheduledExecutorService removeTimer = Executors.newSingleThreadScheduledExecutor();

	public String sendToKafka(String payload) throws InterruptedException {
		var requestId = UUID.randomUUID().toString();
		responseMap.put(requestId, new LinkedBlockingDeque<>(1));
		clearnAction(requestId);
		var record = new ProducerRecord<String, String>(BeanConfig.TOPIC_REQ, requestId, payload);
		record.headers().add(BeanConfig.REQUEST_ID, requestId.getBytes());
		kafkaTemplate.send(record).whenComplete((rs, ex) -> {
			log.info("send to req topic. offset : {}", rs.getRecordMetadata().offset());
		});

		return responseMap.get(requestId).poll(RequestListener.IDLE_TIME + 1, TimeUnit.SECONDS);
	}

	private void clearnAction(String requestId) {
		Runnable task = () -> responseMap.remove(requestId);
		removeTimer.schedule(task, 30, TimeUnit.SECONDS);
	}

}
