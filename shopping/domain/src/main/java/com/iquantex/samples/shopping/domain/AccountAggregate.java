package com.iquantex.samples.shopping.domain;

import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.server.aggregate.ActReturn;
import com.iquantex.phoenix.server.aggregate.CommandHandler;
import com.iquantex.phoenix.server.aggregate.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.QueryHandler;
import com.iquantex.samples.shopping.coreapi.account.AccountCancelCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountCancelOkEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountConfirmCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountConfirmOkEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountPayCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountPayCompensateCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountPayCompensateOkEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountPayFailEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountPayOkEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountQueryCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountQueryEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountTryCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountTryFailEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountTryOkEvent;

import java.io.Serializable;

@EntityAggregateAnnotation(aggregateRootType = "AccountAggregate")
public class AccountAggregate implements Serializable {

	private static final long serialVersionUID = 1989248847513267951L;

	private double amt;

	private double frozenAmt;

	/**
	 * 假定默认1W元
	 */
	public AccountAggregate() {
		this.amt = 10000;
		this.frozenAmt = 0;
	}

	@QueryHandler(aggregateRootId = "accountCode")
	public ActReturn act(AccountQueryCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS)
				.event(new AccountQueryEvent(cmd.getAccountCode(), amt, frozenAmt)).build();
	}

	@CommandHandler(aggregateRootId = "accountCode")
	public ActReturn act(AccountTryCmd cmd) {
		if (amt - frozenAmt > cmd.getFrozenAmt()) {
			return ActReturn.builder().retCode(RetCode.SUCCESS)
					.event(new AccountTryOkEvent(cmd.getAccountCode(), cmd.getFrozenAmt())).build();
		}
		else {
			String retMessage = String.format("资金可用不足，剩余:%f, 当前需要冻结:%f", amt - frozenAmt, cmd.getFrozenAmt());
			return ActReturn.builder().retCode(RetCode.FAIL).retMessage(retMessage)
					.event(new AccountTryFailEvent(cmd.getAccountCode(), cmd.getFrozenAmt(), retMessage)).build();
		}
	}

	public void on(AccountTryOkEvent event) {
		frozenAmt += event.getFrozenAmt();
	}

	public void on(AccountTryFailEvent event) {
	}

	@CommandHandler(aggregateRootId = "accountCode")
	public ActReturn act(AccountConfirmCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS)
				.event(new AccountConfirmOkEvent(cmd.getAccountCode(), cmd.getFrozenAmt())).build();
	}

	public void on(AccountConfirmOkEvent event) {
		amt -= event.getFrozenAmt();
		frozenAmt -= event.getFrozenAmt();
	}

	@CommandHandler(aggregateRootId = "accountCode")
	public ActReturn act(AccountCancelCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS)
				.event(new AccountCancelOkEvent(cmd.getAccountCode(), cmd.getFrozenAmt())).build();
	}

	public void on(AccountCancelOkEvent event) {
		frozenAmt -= event.getFrozenAmt();
	}

	@CommandHandler(aggregateRootId = "accountCode")
	public ActReturn act(AccountPayCmd cmd) {
		if (cmd.getAmt() < 0) {
			throw new RuntimeException("金额不能小于0");
		}
		if (amt > cmd.getAmt()) {
			return ActReturn.builder().retCode(RetCode.SUCCESS)
					.event(new AccountPayOkEvent(cmd.getAccountCode(), cmd.getAmt())).build();
		}
		else {
			String retMessage = String.format("余额不足，剩余:%f, 当前需要:%f", amt, cmd.getAmt());
			return ActReturn.builder().retCode(RetCode.FAIL).retMessage(retMessage)
					.event(new AccountPayFailEvent(cmd.getAccountCode(), cmd.getAmt(), retMessage)).build();
		}
	}

	public void on(AccountPayOkEvent event) {
		amt -= event.getAmt();
	}

	public void on(AccountPayFailEvent event) {
	}

	@CommandHandler(aggregateRootId = "accountCode")
	public ActReturn act(AccountPayCompensateCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS)
				.event(new AccountPayCompensateOkEvent(cmd.getAccountCode(), cmd.getAmt())).build();
	}

	public void on(AccountPayCompensateOkEvent event) {
		amt += event.getAmt();
	}

}
