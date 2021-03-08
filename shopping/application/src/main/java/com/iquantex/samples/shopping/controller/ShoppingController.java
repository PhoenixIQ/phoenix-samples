package com.iquantex.samples.shopping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iquantex.phoenix.client.PhoenixClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("shoppingcart")
public class ShoppingController {

	@Autowired
	private PhoenixClient client;

	@Autowired
	private ObjectMapper objectMapper;

	@PutMapping("/{userId}/{itemId}/{qty}")
	public String allocateBalanceQty(@PathVariable String userId, @PathVariable String itemId, @PathVariable int qty) {
		return null;
	}

	@GetMapping("/{userId}")
	public String queryShoppingCart(@PathVariable String userId) {
		return null;
	}

}
