package com.example.demokafkahttpexpire.listener;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.example.demokafkahttpexpire.config.BeanConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestListener {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public static final int IDLE_TIME = 2;

	@KafkaListener(id = "req", topics = BeanConfig.TOPIC_REQ)
	public void onMessage(ConsumerRecord<String, String> record, @Header(BeanConfig.REQUEST_ID) String requestId) {
		log.info("[REV] req-id: value -> {}:{}", requestId, record.value());
		sleep();
		sendMessageToResponseTopic(requestId, record.value());
	}

	private void sendMessageToResponseTopic(String requestId, String value) {
		var message = String.format("rev time: %s -> %s", LocalTime.now(), value);
		var record = new ProducerRecord<>(BeanConfig.TOPIC_RES, "res", message);
		record.headers().add("correlation-id", requestId.getBytes());
		kafkaTemplate.send(record).whenComplete((rs, ex) -> {
			log.info("sent to reply. offset : {}", rs.getRecordMetadata().offset());
		});
	}

	private void sleep() {
		try {
			TimeUnit.SECONDS.sleep(IDLE_TIME);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
