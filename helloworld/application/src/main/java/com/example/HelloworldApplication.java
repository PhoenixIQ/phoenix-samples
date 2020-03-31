package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@EnableSwagger2
@SpringBootApplication
public class HelloworldApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(HelloworldApplication.class, args);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			System.exit(1);
		}
	}

}
