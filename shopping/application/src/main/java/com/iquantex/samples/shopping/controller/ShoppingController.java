package com.iquantex.samples.shopping.controller;

import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.client.RpcResult;
import com.iquantex.samples.shopping.coreapi.account.AccountQueryCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountQueryEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsQueryCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsQueryEvent;
import com.iquantex.samples.shopping.coreapi.transaction.BuyGoodsCmd;
import com.iquantex.samples.shopping.coreapi.transaction.BuyGoodsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("shopping")
public class ShoppingController {

	private final PhoenixClient client;

	public ShoppingController(PhoenixClient client) {
		this.client = client;
	}

	@PutMapping("/buy_goods/{accountCode}/{goodsCode}/{qty}/{price}")
	public String buyGoods(@PathVariable String accountCode, @PathVariable String goodsCode, @PathVariable int qty,
			@PathVariable double price) {
		BuyGoodsCmd buyGoodsCmd = BuyGoodsCmd.builder().accountCode(accountCode).goodsCode(goodsCode).qty(qty)
				.price(price).build();
		Future<RpcResult> future = client.send(buyGoodsCmd, "shopping", UUID.randomUUID().toString());
		try {
			BuyGoodsEvent buyGoodsEvent = (BuyGoodsEvent) future.get(10, TimeUnit.SECONDS).getData();
			return buyGoodsEvent.getRemark();
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			return "rpc error: " + e.getMessage();
		}
	}

	@GetMapping("account/{accountCode}")
	public String queryAccount(@PathVariable String accountCode) {
		AccountQueryCmd accountQueryCmd = AccountQueryCmd.builder().accountCode(accountCode).build();
		Future<RpcResult> future = client.send(accountQueryCmd, "shopping", UUID.randomUUID().toString());
		try {
			AccountQueryEvent result = (AccountQueryEvent) future.get(10, TimeUnit.SECONDS).getData();
			return "账户余额:" + result.getAmt();
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			return "rpc error: " + e.getMessage();
		}
	}

	@GetMapping("goods/{goodsCode}")
	public String queryGoods(@PathVariable String goodsCode) {
		GoodsQueryCmd goodsQueryCmd = GoodsQueryCmd.builder().goodsCode(goodsCode).build();
		Future<RpcResult> future = client.send(goodsQueryCmd, "shopping", UUID.randomUUID().toString());
		try {
			GoodsQueryEvent result = (GoodsQueryEvent) future.get(10, TimeUnit.SECONDS).getData();
			return "商品余额" + result.getQty();
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			return "rpc error: " + e.getMessage();
		}
	}

}
