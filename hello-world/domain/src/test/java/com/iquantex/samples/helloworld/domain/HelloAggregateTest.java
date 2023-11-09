package com.iquantex.samples.helloworld.domain;

import com.iquantex.phoenix.server.test.EntityAggregateFixture;
import com.iquantex.samples.helloworld.coreapi.Hello;
import com.iquantex.samples.helloworld.domain.entity.HelloAggregate;
import org.junit.Assert;
import org.junit.Test;

/**
 * 账户聚合根单元测试
 */
public class HelloAggregateTest {

	@Test
	public void testHello() {
		EntityAggregateFixture fixture = new EntityAggregateFixture(HelloAggregate.class.getPackage().getName());
		Hello.HelloCmd helloCmd = Hello.HelloCmd.newBuilder().setHelloId("H001").build();
		// 断言事件
		fixture.when(helloCmd).expectRetSuccessCode().expectMessage(Hello.HelloEvent.class);
		HelloAggregate helloAggregate = fixture.getAggregateRoot(HelloAggregate.class, "H001");
		Assert.assertTrue(helloAggregate.getNum() == 1);
	}

}
