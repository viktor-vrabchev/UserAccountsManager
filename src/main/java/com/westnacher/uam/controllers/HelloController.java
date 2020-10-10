package com.westnacher.uam.controllers;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class HelloController {

	@GetMapping(produces = "application/json")
	public ResponseEntity<?> sayHello() {
		return ResponseEntity.ok("HELLO!");
	}

	@GetMapping(path = "/localDateTime", produces = "application/json")
	public ResponseEntity<?> localDateTime() {
		return ResponseEntity.ok(LocalDateTime.now());
	}
}
