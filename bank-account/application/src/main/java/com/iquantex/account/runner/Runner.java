package com.iquantex.account.runner;

import com.iquantex.phoenix.eventpublish.EventPublishWorker;
import com.iquantex.phoenix.server.worker.ServerWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动phoenix
 *
 * @author baozi
 * @date 2020/2/4 2:55 PM
 */
@Slf4j
@Component
public class Runner implements ApplicationRunner {

	/** phoenix服务组件 */
	@Autowired
	private ServerWorker serverWorker;

	@Autowired(required = false)
	EventPublishWorker eventPublishWorker;

	/**
	 * 启动服务
	 */
	@Override
	public void run(ApplicationArguments args) {
		serverWorker.startup();

		if (eventPublishWorker != null) {
			eventPublishWorker.start();
		}
		log.info("app started");
	}

}
