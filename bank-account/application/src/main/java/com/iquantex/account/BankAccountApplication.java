package com.iquantex.account;

import com.iquantex.phoenix.eventpublish.EventPublishWorker;
import com.iquantex.phoenix.server.worker.ServerWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author baozi
 * @date 2020/2/4 10:50 AM
 */
@Slf4j
@SpringBootApplication
public class BankAccountApplication implements ApplicationRunner {

	@Autowired
	private ServerWorker serverWorker;

	@Autowired(required = false)
	private EventPublishWorker eventPublishWorker;

	/**
	 * 启动服务
	 */
	@Override
	public void run(ApplicationArguments args) {
		serverWorker.startup();

		if (eventPublishWorker != null) {
			eventPublishWorker.start();
		}
		log.info("BankAccount app started");
	}

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
