package com.iquantex.phoenix.domain;

import com.iquantex.samples.account.coreapi.Hello;
import com.iquantex.samples.account.domain.HelloAggregate;
import com.iquantex.phoenix.server.test.util.EntityAggregateFixture;
import org.junit.Assert;
import org.junit.Test;

/**
 * 账户聚合根单元测试
 *
 * @author baozi
 * @date 2020/02/10 3:44
 */
public class HelloAggregateTest {

	/**
	 * hello 测试
	 */
	@Test
	public void testHello() {
		EntityAggregateFixture fixture = new EntityAggregateFixture();
		Hello.HelloCmd helloCmd = Hello.HelloCmd.newBuilder().setHelloId("H001").build();
		// 断言
		fixture.when(helloCmd).expectRetSuccessCode().expectMessage(Hello.HelloEvent.class);
		HelloAggregate helloAggregate = fixture.getAggregateRoot(HelloAggregate.class, "H001");
		Assert.assertEquals(1, helloAggregate.getNum());
	}

}
