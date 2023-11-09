package com.iquantex.phoenix.samples.account.domain;

import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.samples.account.api.command.AccountHeartbeatCmd;
import com.iquantex.phoenix.samples.account.api.event.AccountHeartbeatEvent;
import com.iquantex.phoenix.server.aggregate.ActReturn;
import com.iquantex.phoenix.server.aggregate.CommandHandler;
import com.iquantex.phoenix.server.aggregate.cls.AggregateSegment;
import lombok.extern.slf4j.Slf4j;

/** 片段代码, 实现心跳. */
@Slf4j
@AggregateSegment
public class BankAccountHeartbeatBehavior {

	@CommandHandler(aggregateRootId = "accountCode")
	public ActReturn act(AccountHeartbeatCmd cmd) {
		String message = String.format("received heartbeat from %s", cmd.getAccountCode());
		log.info(message);

		return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage(message)
				.event(new AccountHeartbeatEvent(cmd.getAccountCode())).build();
	}

	/**
	 * 空实现
	 * @param event
	 */
	public void on(AccountHeartbeatEvent event) {
	}

}
