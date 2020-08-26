package com.iquantex.samples.shoppingcart.domain.inventory;

import com.iquantex.phoenix.server.test.EntityAggregateFixture;
import com.iquantex.samples.shoppingcart.coreapi.inventory.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * 账户聚合根单元测试
 */
public class InventoryAggregateTest {

	private static final String AGGREGATE_PACKAGE = "com.iquantex.samples.shoppingcart.domain";

	@Test
	public void allocate_success() {
		EntityAggregateFixture fixture = new EntityAggregateFixture(AGGREGATE_PACKAGE);
		InventoryAllocateCmd cmd = InventoryAllocateCmd.builder().itemId("I001").allocateQty(100).build();
		// 断言事件
		fixture.when(cmd).expectRetSuccessCode().expectMessage(InventoryAllocateOkEvent.class);
		InventoryAggregate inventoryAggregate = fixture.getAggregateRoot(InventoryAggregate.class, "I001");
		Assert.assertTrue(inventoryAggregate.getBalanceQty() == 100);
	}

	@Test
	public void allocate_fail() {
		EntityAggregateFixture fixture = new EntityAggregateFixture(AGGREGATE_PACKAGE);
		InventoryAllocateCmd cmd = InventoryAllocateCmd.builder().itemId("I001").allocateQty(-100).build();
		// 断言事件
		fixture.when(cmd).expectRetFailCode().expectMessage(InventoryAllocateFailEvent.class);
		InventoryAggregate inventoryAggregate = fixture.getAggregateRoot(InventoryAggregate.class, "I001");
		Assert.assertTrue(inventoryAggregate.getBalanceQty() == 0);
	}

	@Test
	public void query_sucess() {
		EntityAggregateFixture fixture = new EntityAggregateFixture(AGGREGATE_PACKAGE);
		InventoryAllocateCmd cmd = InventoryAllocateCmd.builder().itemId("I001").allocateQty(100).build();
		fixture.when(cmd).expectRetSuccessCode().expectMessage(InventoryAllocateOkEvent.class);

		InventoryItemQueryCmd inventoryItemQueryCmd = InventoryItemQueryCmd.builder().itemId("I001").build();
		fixture.when(inventoryItemQueryCmd).expectRetSuccessCode().expectMessage(InventoryItemQueryEvent.class);
		InventoryItemQueryEvent inventoryItemQueryEvent = fixture.getLastOutMsg().getPayload();
		Assert.assertTrue(inventoryItemQueryEvent.getBalanceQty() == 100);

	}

}
