package com.iquantex.shoppingcart.domain.shopping;

import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.server.aggregate.entity.CommandHandler;
import com.iquantex.phoenix.server.aggregate.entity.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.entity.QueryHandler;
import com.iquantex.phoenix.server.aggregate.model.ActReturn;
import com.iquantex.shoppingcart.coreapi.shopping.ShoppingCartOptionCmd;
import com.iquantex.shoppingcart.coreapi.shopping.ShoppingCartOptionEvent;
import com.iquantex.shoppingcart.coreapi.shopping.ShoppingCartQueryListCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Map;

/**
 * @author baozi
 * @date 2020/4/2 6:05 PM
 */
@Slf4j
@EntityAggregateAnnotation(aggregateRootType = "shoppingcart")
public class ShoppingCartAggregate implements Serializable {

	/** 用户ID */
	private String userId;

	/** 商品列表 */
	private Map<String/* 商品ID */, Item/* 商品 */> items;

	/** Phoenix客户端 **/
	@Autowired
	private transient PhoenixClient phoenixClient;

	@CommandHandler(aggregateRootId = "userId")
	private ActReturn act(ShoppingCartOptionCmd cmd) {
		return null;
	}

	@QueryHandler(aggregateRootId = "userId")
	private ActReturn act(ShoppingCartQueryListCmd cmd) {
		return null;
	}

	public void on(ShoppingCartOptionEvent event) {

	}

}
