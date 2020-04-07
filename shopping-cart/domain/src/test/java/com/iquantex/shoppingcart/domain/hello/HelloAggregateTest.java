package com.iquantex.shoppingcart.domain.hello;

import com.iquantex.shoppingcart.coreapi.hello.Hello;
import com.iquantex.shoppingcart.domain.hello.entity.HelloAggregate;
import com.iquantex.phoenix.server.test.util.EntityAggregateFixture;
import org.junit.Assert;
import org.junit.Test;

/**
 * 账户聚合根单元测试
 */
public class HelloAggregateTest {

	@Test
	public void testHello() {
		EntityAggregateFixture fixture = new EntityAggregateFixture();
		Hello.HelloCmd helloCmd = Hello.HelloCmd.newBuilder().setHelloId("H001").build();
		// 断言事件
		fixture.when(helloCmd).expectRetSuccessCode().expectMessage(Hello.HelloEvent.class);
		HelloAggregate helloAggregate = fixture.getAggregateRoot(HelloAggregate.class, "H001");
		Assert.assertTrue(helloAggregate.getNum() == 1);
	}

}
