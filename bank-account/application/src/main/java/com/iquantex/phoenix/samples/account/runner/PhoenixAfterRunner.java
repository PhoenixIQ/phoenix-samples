package com.iquantex.phoenix.samples.account.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by lan on 2019/10/10
 */
@Slf4j
@Order(2)
@Component
public class PhoenixAfterRunner implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) {
		log.info("phoenix started");
	}

}
