package com.iquantex.samples.shoppingcart.domain.shopping;

import com.iquantex.phoenix.server.test.util.EntityAggregateFixture;
import com.iquantex.samples.shoppingcart.coreapi.inventory.*;
import com.iquantex.samples.shoppingcart.domain.inventory.InventoryAggregate;
import org.junit.Assert;
import org.junit.Test;

/**
 * 账户聚合根单元测试
 */
public class ShoppingCartAggregateTest {

	@Test
	public void allocate_success() {
		EntityAggregateFixture fixture = new EntityAggregateFixture();
		InventoryAllocateCmd cmd = InventoryAllocateCmd.builder().itemId("I001").allocateQty(100).build();
		// 断言事件
		fixture.when(cmd).expectRetSuccessCode().expectMessage(InventoryAllocateOkEvent.class);
		InventoryAggregate inventoryAggregate = fixture.getAggregateRoot(InventoryAggregate.class, "I001");
		Assert.assertTrue(inventoryAggregate.getBalanceQty() == 100);
	}

	@Test
	public void allocate_fail() {
		EntityAggregateFixture fixture = new EntityAggregateFixture();
		InventoryAllocateCmd cmd = InventoryAllocateCmd.builder().itemId("I001").allocateQty(-100).build();
		// 断言事件
		fixture.when(cmd).expectRetFailCode().expectMessage(InventoryAllocateFailEvent.class);
		InventoryAggregate inventoryAggregate = fixture.getAggregateRoot(InventoryAggregate.class, "I001");
		Assert.assertTrue(inventoryAggregate.getBalanceQty() == 0);
	}

	@Test
	public void query_sucess() {
		EntityAggregateFixture fixture = new EntityAggregateFixture();
		InventoryAllocateCmd cmd = InventoryAllocateCmd.builder().itemId("I001").allocateQty(100).build();
		fixture.when(cmd).expectRetSuccessCode().expectMessage(InventoryAllocateOkEvent.class);

		InventoryItemQueryCmd inventoryItemQueryCmd = InventoryItemQueryCmd.builder().itemId("I001").build();
		fixture.when(inventoryItemQueryCmd).expectRetSuccessCode().expectMessage(InventoryItemQueryEvent.class);
		InventoryItemQueryEvent inventoryItemQueryEvent = fixture.getLastOutMsg().getPayload();
		Assert.assertTrue(inventoryItemQueryEvent.getBalanceQty() == 100);

	}

}
