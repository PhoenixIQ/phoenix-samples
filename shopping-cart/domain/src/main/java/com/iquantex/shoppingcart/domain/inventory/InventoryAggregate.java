package com.iquantex.shoppingcart.domain.inventory;

import com.iquantex.phoenix.server.aggregate.entity.CommandHandler;
import com.iquantex.phoenix.server.aggregate.entity.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.entity.QueryHandler;
import com.iquantex.phoenix.server.aggregate.model.ActReturn;
import com.iquantex.phoenix.server.aggregate.model.RetCode;
import com.iquantex.shoppingcart.coreapi.inventory.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2020/4/2 4:51 PM
 */
@Slf4j
@EntityAggregateAnnotation(aggregateRootType = "inventory")
public class InventoryAggregate implements Serializable {

	/** 商品ID **/
	private String itemId;

	/** 商品库存数量 **/
	@Getter
	private int balanceQty;

	@CommandHandler(aggregateRootId = "itemId")
	public ActReturn act(InventoryAllocateCmd cmd) {
		if (balanceQty + cmd.getAllocateQty() < 0) {
			String retMessage = String.format("库存调整失败,库存不足: 库存余额:%d, 调整数量：%d", balanceQty, cmd.getAllocateQty());
			return ActReturn.builder().retCode(RetCode.FAIL).retMessage(retMessage).event(InventoryAllocateFailEvent
					.builder().itemId(cmd.getItemId()).allocateQty(cmd.getAllocateQty()).build()).build();
		}
		else {
			String retMessage = String.format("库存调整成功: 库存余额:%d, 调整数量：%d", balanceQty, cmd.getAllocateQty());
			return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage(retMessage).event(InventoryAllocateOkEvent
					.builder().itemId(cmd.getItemId()).allocateQty(cmd.getAllocateQty()).build()).build();

		}

	}

	@QueryHandler(aggregateRootId = "itemId")
	public ActReturn act(InventoryItemQueryCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage("查询成功")
				.event(InventoryItemQueryEvent.builder().itemId(cmd.getItemId()).balanceQty(balanceQty).build())
				.build();
	}

	public void on(InventoryAllocateOkEvent event) {
		this.itemId = event.getItemId();
		this.balanceQty += event.getAllocateQty();
	}

	public void on(InventoryAllocateFailEvent event) {
		// not do something.
	}

}
