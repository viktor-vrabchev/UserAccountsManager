package com.westnacher.uam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.westnacher.uam")
public class UamApplication {

	public static void main(String[] args) {
		SpringApplication.run(UamApplication.class, args);
	}

}
