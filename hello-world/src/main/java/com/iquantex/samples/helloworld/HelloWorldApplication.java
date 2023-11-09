package com.iquantex.samples.helloworld;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class HelloWorldApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(HelloWorldApplication.class, args);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			System.exit(1);
		}
	}

}