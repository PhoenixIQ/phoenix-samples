package com.iquantex.phoenix.domain;

import com.iquantex.samples.account.coreapi.command.AccountAllocateCmd;
import com.iquantex.samples.account.coreapi.event.AccountAllocateFailEvent;
import com.iquantex.phoenix.server.test.util.EntityAggregateFixture;
import org.junit.Test;

/**
 * 银行转账测试类
 *
 * @author yanliang
 * @date 2020/3/10 16:47
 */
public class BankAccountAggregateTest {

	/**
	 * 划拨失败
	 */
	@Test
	public void allocate_exceptFail() {
		EntityAggregateFixture fixture = new EntityAggregateFixture();
		// 向 A0 账户划拨 -1500 元，期待划拨失败
		AccountAllocateCmd cmd = new AccountAllocateCmd("A0", -1500);
		// 断言
		fixture.when(cmd).expectRetFailCode().expectMessage(AccountAllocateFailEvent.class);
	}

}
