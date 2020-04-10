package com.iquantex.shoppingcart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.client.RpcResult;
import com.iquantex.shoppingcart.coreapi.inventory.InventoryAllocateCmd;
import com.iquantex.shoppingcart.coreapi.inventory.InventoryItemQueryCmd;
import com.iquantex.shoppingcart.coreapi.inventory.InventoryItemQueryEvent;
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
@RequestMapping("inventory")
public class InventoryController {

	@Autowired
	private PhoenixClient client;

	@Autowired
	private ObjectMapper objectMapper;

	@PutMapping("/{itemId}/{allocateQty}")
	public String allocateBalanceQty(@PathVariable String itemId, @PathVariable int allocateQty) {
		InventoryAllocateCmd inventoryAllocateCmd = InventoryAllocateCmd.builder().itemId(itemId)
				.allocateQty(allocateQty).build();

		Future<RpcResult> future = client.send(inventoryAllocateCmd, UUID.randomUUID().toString());
		try {
			RpcResult result = future.get(10, TimeUnit.SECONDS);
			return result.getMessage();
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			return "rpc error: " + e.getMessage();
		}
	}

	@GetMapping("/{itemId}")
	public String queryBalanceQty(@PathVariable String itemId) {
		InventoryItemQueryCmd inventoryItemQueryCmd = InventoryItemQueryCmd.builder().itemId(itemId).build();

		Future<RpcResult> future = client.send(inventoryItemQueryCmd, UUID.randomUUID().toString());
		try {
			RpcResult result = future.get(10, TimeUnit.SECONDS);
			InventoryItemQueryEvent inventoryItemQueryEvent = (InventoryItemQueryEvent) result.getData();
			return objectMapper.writeValueAsString(inventoryItemQueryEvent);
		}
		catch (InterruptedException | ExecutionException | TimeoutException | JsonProcessingException e) {
			return "rpc error: " + e.getMessage();
		}
	}

}
