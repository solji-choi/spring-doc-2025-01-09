package com.ll.spring_doc_2025_01_09;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringDoc20250109Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringDoc20250109Application.class, args);
	}

}
