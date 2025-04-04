package com.dementor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
public class DeMentorApplication {
	// 개발 세팅 중
	public static void main(String[] args) {
		SpringApplication.run(DeMentorApplication.class, args);
	}

}
