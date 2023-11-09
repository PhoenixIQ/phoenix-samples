package com.iquantex.phoenix.samples.account.domain;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;
import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.samples.account.api.command.AccountAllocateCmd;
import com.iquantex.phoenix.samples.account.api.command.AccountQueryCmd;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateFailEvent;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateOkEvent;
import com.iquantex.phoenix.samples.account.api.event.AccountQueryEvent;
import com.iquantex.phoenix.samples.account.api.protobuf.Account;
import com.iquantex.phoenix.server.aggregate.ActReturn;
import com.iquantex.phoenix.server.aggregate.CommandHandler;
import com.iquantex.phoenix.server.aggregate.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.QueryHandler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * * Created by lan on 2019/10/10
 */
@EntityAggregateAnnotation(aggregateRootType = "BankAccount", idempotentSize = 100, bloomSize = 100000)
@Getter
@Setter
@Slf4j
public class BankAccountAggregate implements Serializable {

	private static final String MONITORY_TIME_OUT_ACCOUNT = "monitor_retry";

	private static final long serialVersionUID = 6073238164083701075L;

	/** 账户代码 */
	private String account;

	/** 账户余额 */
	private double balanceAmt;

	/** 成功转出次数 */
	private int successTransferOut;

	/** 失败转出次数 */
	private int failTransferOut;

	/** 成功转入次数 */
	private int successTransferIn;

	/** 是否允许请求通过 */
	@Autowired
	private transient MockService mockService;

	@CommandHandler(aggregateRootId = "accountCode", isCommandSourcing = true)
	public ActReturn act(Account.AccountCreateCmd createCmd) {
		this.account = createCmd.getAccountCode();
		this.balanceAmt = createCmd.getBalanceAmt();

		String message = String.format("初始化账户代码<%s>, 初始化余额<%s>. ", createCmd.getAccountCode(),
				createCmd.getBalanceAmt());

		return ActReturn
				.builder().retCode(RetCode.SUCCESS).retMessage(message).event(Account.AccountCreateEvent.newBuilder()
						.setAccountCode(createCmd.getAccountCode()).setBalanceAmt(createCmd.getBalanceAmt()).build())
				.build();
	}

	/**
	 * 处理查询命令
	 * @param cmd
	 * @return
	 */
	@QueryHandler(aggregateRootId = "accountCode")
	public ActReturn act(AccountQueryCmd cmd) {

		return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage("查询成功").event(
				new AccountQueryEvent(account, balanceAmt, successTransferOut, failTransferOut, successTransferIn))
				.build();
	}

	/**
	 * 处理账户划拨命令
	 * @param cmd
	 * @return
	 */
	@CommandHandler(aggregateRootId = "accountCode", enableCreateAggregate = false,
			idempotentIds = { "AccountAllocateCmd", "accountCode", "allocateNumber" })
	public ActReturn act(AccountAllocateCmd cmd) {

		// 模拟处理超时堵塞65s
		if (cmd.getAccountCode().equals(MONITORY_TIME_OUT_ACCOUNT)) {
			Uninterruptibles.sleepUninterruptibly(65, TimeUnit.SECONDS);
		}

		// 模拟调用其他service
		if (!mockService.isPass()) {
			String retMessage = "账户划拨失败,请求判定不通过.";
			return ActReturn.builder().retCode(RetCode.FAIL).retMessage(retMessage)
					.event(new AccountAllocateFailEvent(cmd.getAccountCode(), cmd.getAmt(), retMessage)).build();
		}

		if (Math.abs(cmd.getAmt()) < 0.00000001) {
			String retMessage = String.format("账户划拨失败,划拨金额不可为0: 账户余额:%f, 划拨金额：%f", balanceAmt, cmd.getAmt());
			return ActReturn.builder().retCode(RetCode.FAIL).retMessage(retMessage)
					.event(new AccountAllocateFailEvent(cmd.getAccountCode(), cmd.getAmt(), retMessage)).build();
		}
		else if (balanceAmt + cmd.getAmt() < 0) {
			String retMessage = String.format("账户划拨失败,账户余额不足: 账户余额:%f, 划拨金额：%f", balanceAmt, cmd.getAmt());
			return ActReturn.builder().retCode(RetCode.FAIL).retMessage(retMessage)
					.event(new AccountAllocateFailEvent(cmd.getAccountCode(), cmd.getAmt(), retMessage)).build();
		}
		else {
			String retMessage = String.format("账户划拨成功：划拨金额：%.2f，账户余额：%.2f", cmd.getAmt(), balanceAmt + cmd.getAmt());
			return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage(retMessage)
					.event(new AccountAllocateOkEvent(cmd.getAccountCode(), cmd.getAmt())).build();
		}
	}

	/**
	 * 处理账户划拨成功事件
	 * @param event
	 */
	public void on(AccountAllocateOkEvent event) {
		balanceAmt += event.getAmt();
		if (event.getAmt() < 0) {
			successTransferOut++;
		}
		else {
			successTransferIn++;
		}
	}

	/**
	 * 处理账户划拨失败事件
	 * @param event
	 */
	public void on(AccountAllocateFailEvent event) {
		failTransferOut++;
	}

}
