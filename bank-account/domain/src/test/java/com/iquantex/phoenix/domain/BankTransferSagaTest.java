package com.iquantex.phoenix.domain;

import com.iquantex.phoenix.server.aggregate.model.RetCode;
import com.iquantex.phoenix.server.message.Message;
import com.iquantex.phoenix.server.message.MessageFactory;
import com.iquantex.phoenix.server.message.Router;
import com.iquantex.phoenix.transaction.test.util.TransactionAggregateFixture;
import com.iquantex.samples.account.coreapi.command.AccountAllocateCmd;
import com.iquantex.samples.account.coreapi.command.AccountTransferCmd;
import com.iquantex.samples.account.coreapi.event.AccountAllocateFailEvent;
import com.iquantex.samples.account.coreapi.event.AccountAllocateOkEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sun on 2020/2/14.
 */
public class BankTransferSagaTest {

	private final static String inAccountCode = "test_in";

	private final static String outAccountCode = "test_out";

	private TransactionAggregateFixture testFixture;

	Message reqMsg;

	@Before
	public void init() {
		testFixture = new TransactionAggregateFixture();

		Map<String /* msgName */, String /* dst */> routerTable = new HashMap<>();
		routerTable.put(AccountAllocateCmd.class.getName(), "loca/TA/BankTransferSaga");
		routerTable.put(AccountAllocateFailEvent.class.getName(), "local/TA/BankTransferSaga");
		routerTable.put(AccountAllocateOkEvent.class.getName(), "local/TA/BankTransferSaga");

		Router.getInstance().init(routerTable);
	}

	/**
	 * 事务start，收到转账请求会发出cmd
	 */
	@Test
	public void trans_start_transaction() {
		int tranAmt = 100;
		AccountTransferCmd req = new AccountTransferCmd(inAccountCode, outAccountCode, tranAmt);
		reqMsg = MessageFactory.getRequestMsg(req);
		testFixture.when(reqMsg).expectMessage(AccountAllocateCmd.class);
	}

	/**
	 * 处理转出ok
	 */
	@Test
	public void trans_out_ok() {
		int tranAmt = 100;
		AccountTransferCmd req = new AccountTransferCmd(inAccountCode, outAccountCode, tranAmt);
		reqMsg = MessageFactory.getRequestMsg(req);

		// 待发出去给c端的转账cmd
		Message transOutCmdMsg = testFixture.when(reqMsg).getLastOutMsg();

		// 手工模拟构建从c端发出来的event
		AccountAllocateOkEvent transOutOkEvent = new AccountAllocateOkEvent(outAccountCode, -tranAmt);
		Message transOutOkEventMsg = MessageFactory.getEventMsg(RetCode.SUCCESS, "", transOutOkEvent, transOutCmdMsg);

		// 根据上述"从c端发出来的"event,经过saga的处理，再产生一个cmd，发给c端。
		testFixture.when(transOutOkEventMsg).expectMessage(AccountAllocateCmd.class).expectRetCode(RetCode.NONE);
	}

	/**
	 * 处理转入ok 该方法也是事务完成的测试
	 */
	@Test
	public void trans_in_ok() {
		int tranAmt = 100;
		AccountTransferCmd req = new AccountTransferCmd(inAccountCode, outAccountCode, tranAmt);
		reqMsg = MessageFactory.getRequestMsg(req);

		testFixture.when(reqMsg).getLastOutMsg();

		AccountAllocateCmd transInCmd = new AccountAllocateCmd(inAccountCode, tranAmt);
		Message transInCmdMsg = MessageFactory.getCmdMsg(transInCmd);

		// 手工模拟构建从c端发出来的转入成功event
		AccountAllocateOkEvent transInOkEvent = new AccountAllocateOkEvent(inAccountCode, tranAmt);
		Message transInOkEventMsg = MessageFactory.getEventMsg(RetCode.SUCCESS, "", transInOkEvent, transInCmdMsg);

		// 赋值，方便找聚合根
		transInOkEventMsg.toBuilder().setTransId(reqMsg.getTransId());
		transInOkEventMsg.toBuilder().setDst(reqMsg.getDst());

		// 根据上述"从c端发出来的"event,经过saga的处理，此时事务结束，不会再产生cmd.
		testFixture.when(transInOkEventMsg).expectNull();
	}

	/**
	 * 测试转出失败
	 */
	@Test
	public void trans_out_fail() {
		int tranAmt = 1100;
		AccountTransferCmd req = new AccountTransferCmd(inAccountCode, outAccountCode, tranAmt);
		reqMsg = MessageFactory.getRequestMsg(req);

		// 待发出去给c端的转账cmd
		Message transOutCmdMsg = testFixture.when(reqMsg).getLastOutMsg();

		// 手工模拟构建从c端发出来的event
		AccountAllocateFailEvent transOutFailEvent = new AccountAllocateFailEvent();
		Message transOutFailEventMsg = MessageFactory.getEventMsg(RetCode.FAIL, "", transOutFailEvent, transOutCmdMsg);

		// 根据上述"从c端发出来的"event,经过saga的处理，因为传出失败，不会再产生cmd。
		testFixture.when(transOutFailEventMsg).expectNull();
	}

}
