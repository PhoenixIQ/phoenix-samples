package com.iquantex.samples.shoppingcart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.client.RpcResult;
import com.iquantex.samples.shoppingcart.coreapi.shopping.ShoppingCartQueryListCmd;
import com.iquantex.samples.shoppingcart.coreapi.shopping.ShoppingCartOptionCmd;
import com.iquantex.samples.shoppingcart.coreapi.shopping.ShoppingCartQueryListEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("shoppingcart")
public class ShoppingCartController {

	@Autowired
	private PhoenixClient client;

	@Autowired
	private ObjectMapper objectMapper;

	@PutMapping("/{userId}/{itemId}/{qty}")
	public String allocateBalanceQty(@PathVariable String userId, @PathVariable String itemId, @PathVariable int qty) {
		ShoppingCartOptionCmd shoppingCartOptionCmd = ShoppingCartOptionCmd.builder().userId(userId).itemId(itemId)
				.qty(qty).build();

		Future<RpcResult> future = client.send(shoppingCartOptionCmd, "shopping-cart", UUID.randomUUID().toString());
		try {
			RpcResult result = future.get(10, TimeUnit.SECONDS);
			return result.getMessage();
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			return "rpc error: " + e.getMessage();
		}
	}

	@GetMapping("/{userId}")
	public String queryShoppingCart(@PathVariable String userId) {
		ShoppingCartQueryListCmd shoppingCartQueryListCmd = ShoppingCartQueryListCmd.builder().userId(userId).build();

		Future<RpcResult> future = client.send(shoppingCartQueryListCmd, "shopping-cart", UUID.randomUUID().toString());
		try {
			RpcResult result = future.get(10, TimeUnit.SECONDS);
			ShoppingCartQueryListEvent shoppingCartQueryListEvent = (ShoppingCartQueryListEvent) result.getData();
			return objectMapper.writeValueAsString(shoppingCartQueryListEvent);
		}
		catch (InterruptedException | ExecutionException | TimeoutException | JsonProcessingException e) {
			return "rpc error: " + e.getMessage();
		}
	}

}
