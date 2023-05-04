package com.example.demokafkahttpexpire.web;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demokafkahttpexpire.service.SampleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class SampleController {

	private final ConcurrentMap<String, BlockingDeque<String>> responseMap;

	private final SampleService sampleService;

	@PostMapping("/send")
	public ResponseEntity<?> send(@RequestBody String body) {
		try {
			var response = sampleService.sendToKafka(body);
			return ResponseEntity.ok().body(response);
		}
		catch (InterruptedException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/map-size")
	public ResponseEntity<?> status() {
		return ResponseEntity.ok().body("request size: " + responseMap.size());
	}

}
