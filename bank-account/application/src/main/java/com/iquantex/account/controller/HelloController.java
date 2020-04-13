package com.iquantex.account.controller;

import com.iquantex.samples.account.coreapi.Hello;
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

/**
 * hello controller类
 *
 * @author zhengjie.shen
 * @date 2020/3/10 16:45
 */
@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloController {

	/** phoenix客户端 */
	@Autowired
	private PhoenixClient client;

	/**
	 * hello 指令信息
	 * @param helloId hello 信息
	 * @return 结果信息
	 */
	@PutMapping("/{helloId}")
	public String allocate(@PathVariable String helloId) {
		Hello.HelloCmd helloCmd = Hello.HelloCmd.newBuilder().setHelloId(helloId).build();
		Future<RpcResult> future = client.send(helloCmd, "");
		try {
			RpcResult result = future.get(10, TimeUnit.SECONDS);
			return result.getMessage();
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			return "rpc error: " + e.getMessage();
		}
	}

}
