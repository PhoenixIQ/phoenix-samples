package com.iquantex.phoenix.samples.account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Created by lan on 2019/10/10
 */
@Slf4j
@EnableEurekaClient
@SpringBootApplication
public class BankAccountApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(BankAccountApplication.class, args);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			System.exit(1);
		}
	}

}