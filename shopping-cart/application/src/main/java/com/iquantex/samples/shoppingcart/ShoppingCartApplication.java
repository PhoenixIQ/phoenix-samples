package com.iquantex.samples.shoppingcart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@EnableSwagger2
@SpringBootApplication
public class ShoppingCartApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(ShoppingCartApplication.class, args);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			System.exit(1);
		}
	}

}
