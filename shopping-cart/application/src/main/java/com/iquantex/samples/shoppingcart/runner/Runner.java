package com.iquantex.samples.shoppingcart.runner;

import com.iquantex.phoenix.server.worker.ServerWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 运行phoenix
 */
@Slf4j
@Component
public class Runner implements ApplicationRunner {

	/** phoenix服务工作组件 */
	@Autowired
	private ServerWorker serverWorker;

	/**
	 * 启动phoenix服务
	 */
	@Override
	public void run(ApplicationArguments args) {
		serverWorker.startup();
		log.info("app started");
	}

}
