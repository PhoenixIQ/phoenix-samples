package com.iquantex.samples.helloworld.controller;

import com.iquantex.samples.helloworld.coreapi.Hello;
import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.client.RpcResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloController {

	/**
	 * phoenix 客户端
	 */
	@Autowired
	private PhoenixClient client;

	/**
	 * 处理 hello Http请求
	 * @return 指令返回结果
	 */
	@PutMapping("/{helloId}")
	public String allocate(@PathVariable String helloId) {
		Hello.HelloCmd helloCmd = Hello.HelloCmd.newBuilder().setHelloId(helloId).build();
		// 发送指令信息
		Future<RpcResult> future = client.send(helloCmd, "helloworld", "");
		try {
			RpcResult result = future.get(10, TimeUnit.SECONDS);
			return result.getMessage();
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			return "rpc error: " + e.getMessage();
		}
	}

}
