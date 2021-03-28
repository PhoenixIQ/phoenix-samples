package com.iquantex.phoenix.samples.account;

import java.util.List;

import com.iquantex.phoenix.core.message.Message;
import com.iquantex.phoenix.core.message.MessageFactory;
import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.samples.account.api.command.AccountAllocateCmd;
import com.iquantex.phoenix.samples.account.api.command.AccountTransferCmd;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateFailEvent;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateOkEvent;
import com.iquantex.phoenix.transaction.test.TransactionAggregateFixture;

import org.junit.Test;

/**
 * Created by Sun on 2020/2/14.
 */
public class BankTransferSagaTest {

	private final static String inAccountCode = "test_in";

	private final static String outAccountCode = "test_out";

	private final TransactionAggregateFixture testFixture = new TransactionAggregateFixture(
			"com.iquantex.phoenix.samples.account.saga");

	private Message reqMsg;

	/**
	 * 事务start，收到转账请求会发出cmd
	 */
	@Test
	public void trans_start_transaction() {
		int tranAmt = 100;
		AccountTransferCmd req = new AccountTransferCmd(inAccountCode, outAccountCode, tranAmt);
		reqMsg = MessageFactory.getCmdMsg("local", "client", req);
		testFixture.when(reqMsg).expectMessageSize(1);
	}

	/**
	 * 处理转出ok
	 */
	@Test
	public void trans_out_ok() {
		int tranAmt = 100;
		AccountTransferCmd req = new AccountTransferCmd(inAccountCode, outAccountCode, tranAmt);
		reqMsg = MessageFactory.getCmdMsg("local", "client", req);

		// 待发出去给c端的转账cmd
		Message transOutCmdMsg = null;
		List<Message> outMsgList = testFixture.when(reqMsg).getOutMsgList();
		for (Message message : outMsgList) {
			if (message.getPayloadClassName().equals(AccountAllocateCmd.class.getName())) {
				transOutCmdMsg = message;
			}
		}

		// 手工模拟构建从c端发出来的event
		AccountAllocateOkEvent transOutOkEvent = new AccountAllocateOkEvent(outAccountCode, -tranAmt);
		Message transOutOkEventMsg = MessageFactory.getEventMsg(RetCode.SUCCESS, "", transOutOkEvent, transOutCmdMsg);

		// 根据上述"从c端发出来的"event,经过saga的处理，再产生一个cmd，发给c端。
		testFixture.when(transOutOkEventMsg).expectSingleMessageClass(AccountAllocateCmd.class);
	}

	/**
	 * 处理转入ok 该方法也是事务完成的测试
	 */
	@Test
	public void trans_in_ok() {
		int tranAmt = 100;
		AccountTransferCmd req = new AccountTransferCmd(inAccountCode, outAccountCode, tranAmt);
		reqMsg = MessageFactory.getCmdMsg("local", "client", req);

		Message transInCmdMsg = null;
		List<Message> outMsgList = testFixture.when(reqMsg).expectMessageSize(1).getOutMsgList();
		for (Message message : outMsgList) {
			if (message.getPayloadClassName().equals(AccountAllocateCmd.class.getName())) {
				transInCmdMsg = message;
			}
		}

		// 手工模拟构建从c端发出来的转入成功event
		AccountAllocateOkEvent transInOkEvent = new AccountAllocateOkEvent(inAccountCode, tranAmt);
		Message transInOkEventMsg = MessageFactory.getEventMsg(RetCode.SUCCESS, "", transInOkEvent, transInCmdMsg);

		// 根据上述"从c端发出来的"event,经过saga的处理，此时事务结束，不会再产生cmd.
		testFixture.when(transInOkEventMsg).expectNoMessage();
	}

	/**
	 * 测试转出失败
	 */
	@Test
	public void trans_out_fail() {
		int tranAmt = 1100;
		AccountTransferCmd req = new AccountTransferCmd(inAccountCode, outAccountCode, tranAmt);
		reqMsg = MessageFactory.getCmdMsg("local", "client", req);

		// 待发出去给c端的转账cmd
		Message transOutCmdMsg = null;
		List<Message> outMsgList = testFixture.when(reqMsg).getOutMsgList();
		for (Message message : outMsgList) {
			if (message.getPayloadClassName().equals(AccountAllocateCmd.class.getName())) {
				transOutCmdMsg = message;
			}
		}

		// 手工模拟构建从c端发出来的event
		AccountAllocateFailEvent transOutFailEvent = new AccountAllocateFailEvent();
		Message transOutFailEventMsg = MessageFactory.getEventMsg(RetCode.FAIL, "", transOutFailEvent, transOutCmdMsg);

		// 根据上述"从c端发出来的"event,经过saga的处理，因为传出失败，不会再产生cmd。
		testFixture.when(transOutFailEventMsg).expectNoMessage();
	}

}
