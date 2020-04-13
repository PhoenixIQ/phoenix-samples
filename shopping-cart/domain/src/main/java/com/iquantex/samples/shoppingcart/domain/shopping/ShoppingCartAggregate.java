package com.iquantex.samples.shoppingcart.domain.shopping;

import com.iquantex.phoenix.server.aggregate.entity.CommandHandler;
import com.iquantex.phoenix.server.aggregate.entity.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.entity.QueryHandler;
import com.iquantex.phoenix.server.aggregate.model.ActReturn;
import com.iquantex.phoenix.server.aggregate.model.RetCode;
import com.iquantex.samples.shoppingcart.coreapi.shopping.*;
import com.iquantex.samples.shoppingcart.domain.shopping.service.InventoryItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private Map<String/* 商品ID */, ItemEntity/* 商品 */> items = new HashMap<>();

	/** 库存领域服务 **/
	@Autowired
	private transient InventoryItemService inventoryItemService;

	/**
	 * 购物车调整命令处理
	 * @param cmd
	 * @return
	 */
	@CommandHandler(aggregateRootId = "userId")
	public ActReturn act(ShoppingCartOptionCmd cmd) {

		ItemEntity itemEntity = items.getOrDefault(cmd.getItemId(), new ItemEntity(cmd.getItemId(), 0));

		// 1. 扣减购买数量逻辑校验: 购物车中不存在该商品
		if (itemEntity.canOptionQty(cmd.getQty())) {
			return ActReturn.builder().retCode(RetCode.FAIL).retMessage("购物车调整失败: 不存在该商品,不能减少数量")
					.event(ShoppingCartOptionFailEvent.builder().userId(cmd.getUserId()).itemId(cmd.getItemId())
							.qty(cmd.getQty()).build())
					.build();
		}

		// 2. 增加购买数量逻辑校验: 库存是否充足
		if (cmd.getQty() > 0 && !inventoryItemService.inventoryAdequacy(itemEntity.getItemId(), itemEntity.getQty())) {
			return ActReturn
					.builder().retCode(RetCode.FAIL).retMessage("购物车调整失败: 库存数量不够").event(ShoppingCartOptionFailEvent
							.builder().userId(cmd.getUserId()).itemId(cmd.getItemId()).qty(cmd.getQty()).build())
					.build();
		}

		// 3. 正常处理: 返回事件
		return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage("购物车调整成功").event(ShoppingCartOptionEvent
				.builder().userId(cmd.getUserId()).itemId(cmd.getItemId()).qty(cmd.getQty()).build()).build();
	}

	/**
	 * 购物车查询命令处理
	 * @param cmd
	 * @return
	 */
	@QueryHandler(aggregateRootId = "userId")
	public ActReturn act(ShoppingCartQueryListCmd cmd) {
		List<ShoppingCartQueryListEvent.ItemRsp> itemRsps = new ArrayList<>();
		for (ItemEntity itemEntity : items.values()) {
			itemRsps.add(new ShoppingCartQueryListEvent.ItemRsp(itemEntity.getItemId(), itemEntity.getQty(),
					inventoryItemService.inventoryAdequacy(itemEntity.getItemId(), itemEntity.getQty())));

		}
		return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage("购物车查询成功")
				.event(ShoppingCartQueryListEvent.builder().userId(userId).itemRspList(itemRsps).build()).build();
	}

	/**
	 * 购物车调整成功事件处理
	 * @param event
	 */
	public void on(ShoppingCartOptionEvent event) {
		this.userId = event.getUserId();
		ItemEntity itemEntity = items.get(event.getItemId());
		if (itemEntity == null) {
			itemEntity = new ItemEntity(event.getItemId(), 0);
			items.put(itemEntity.getItemId(), itemEntity);
		}
		itemEntity.optionQty(event.getQty());
	}

	/**
	 * 购物车调整失败事件处理
	 * @param event
	 */
	public void on(ShoppingCartOptionFailEvent event) {
		// not doing something...
	}

}
