package com.iquantex.samples.helloworld.controller;

import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.client.RpcResult;
import com.iquantex.samples.helloworld.coreapi.HelloCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@RestController
public class HelloController {

	@Value("${spring.application.name}")
	private String topic;

	@Autowired
	private PhoenixClient client;

	@GetMapping("/hello/{helloId}")
	public String send(@PathVariable String helloId) throws ExecutionException, InterruptedException {
		HelloCmd helloCmd = new HelloCmd(helloId);
		Future<RpcResult> future = client.send(helloCmd, topic, "");
		return future.get().getMessage();
	}

}
