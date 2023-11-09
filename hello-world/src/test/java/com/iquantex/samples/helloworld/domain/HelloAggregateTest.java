package com.iquantex.samples.helloworld.domain;

import com.iquantex.phoenix.server.test.EntityAggregateFixture;
import com.iquantex.samples.helloworld.coreapi.HelloCmd;
import com.iquantex.samples.helloworld.coreapi.HelloEvent;
import org.junit.Assert;
import org.junit.Test;

/**
 * 聚合根单元测试
 */
public class HelloAggregateTest {

	@Test
	public void testHello() {
		EntityAggregateFixture fixture = new EntityAggregateFixture(HelloAggregate.class.getPackage().getName());
		HelloCmd helloCmd = new HelloCmd("H001");
		// 断言事件
		fixture.when(helloCmd).expectRetSuccessCode().expectMessage(HelloEvent.class);
		HelloAggregate helloAggregate = fixture.getAggregateRoot(HelloAggregate.class, "H001");
		Assert.assertTrue(helloAggregate.getNum() == 1);
	}

}