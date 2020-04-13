package com.iquantex.phoenix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author baozi
 * @date 2020/2/4 10:50 AM
 */
@Slf4j
@EnableSwagger2
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
