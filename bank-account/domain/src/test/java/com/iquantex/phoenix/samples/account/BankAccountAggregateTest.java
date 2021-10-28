package com.iquantex.phoenix.samples.account;

import com.iquantex.phoenix.samples.account.api.command.AccountAllocateCmd;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateFailEvent;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateOkEvent;
import com.iquantex.phoenix.samples.account.api.protobuf.Account;
import com.iquantex.phoenix.samples.account.domain.BankAccountAggregate;
import com.iquantex.phoenix.samples.account.domain.MockService;
import com.iquantex.phoenix.server.test.EntityAggregateFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by Sun on 2020/2/14.
 */
public class BankAccountAggregateTest {

	private final static String accountCode = "test";

	private EntityAggregateFixture testFixture;

	private MockService mockService;

	@Before
	public void init() {
		testFixture = new EntityAggregateFixture(BankAccountAggregate.class.getPackage().getName());
		Account.AccountCreateCmd createCmd = Account.AccountCreateCmd.newBuilder().setAccountCode(accountCode)
				.setBalanceAmt(1000).build();
		// mock 聚合根类中注入的Spring service，返回值先给定 true，以进行正常的业务单元测试
		mockService = Mockito.mock(MockService.class);
		Mockito.when(mockService.isPass()).thenReturn(true);
		testFixture.mockBean("mockService", mockService);
		testFixture.when(createCmd).expectRetSuccessCode();
	}

	/**
	 * 转入测试，成功
	 */
	@Test
	public void trans_in_ok() {
		AccountAllocateCmd cmd = new AccountAllocateCmd(accountCode, 100);
		testFixture.when(cmd).expectMessage(AccountAllocateOkEvent.class).expectRetSuccessCode().printIdentify();
	}

	/**
	 * 转出测试，成功
	 */
	@Test
	public void trans_out_ok() {
		AccountAllocateCmd cmd = new AccountAllocateCmd(accountCode, -100);
		testFixture.when(cmd).expectMessage(AccountAllocateOkEvent.class).expectRetSuccessCode().printIdentify();
	}

	/**
	 * 转出测试，业务失败
	 */
	@Test
	public void trans_out_business_fail() {
		AccountAllocateCmd cmd = new AccountAllocateCmd(accountCode, -1100);
		testFixture.when(cmd).expectMessage(AccountAllocateFailEvent.class).expectRetFailCode().printIdentify();
	}

	/**
	 * 转出测试，开关判定不通过，失败 以此示范单元测试中mock spring bean
	 */
	@Test
	public void trans_out_notPass_fail() {
		// mock 聚合根类中注入的Spring service，返回值给定false，用断言错误事件来证明mock bean的注入成功
		Mockito.when(mockService.isPass()).thenReturn(false);
		AccountAllocateCmd cmd = new AccountAllocateCmd(accountCode, -100);
		testFixture.when(cmd).expectMessage(AccountAllocateFailEvent.class).expectRetFailCode().printIdentify();
	}

}
