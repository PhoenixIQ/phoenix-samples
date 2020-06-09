package com.iquantex.samples.account.domain;

import com.iquantex.phoenix.server.aggregate.entity.QueryHandler;
import com.iquantex.samples.account.coreapi.command.AccountAllocateCmd;
import com.iquantex.samples.account.coreapi.command.AccountQueryCmd;
import com.iquantex.samples.account.coreapi.event.AccountAllocateFailEvent;
import com.iquantex.samples.account.coreapi.event.AccountAllocateOkEvent;
import com.iquantex.phoenix.server.aggregate.entity.CommandHandler;
import com.iquantex.phoenix.server.aggregate.entity.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.model.ActReturn;
import com.iquantex.phoenix.server.aggregate.model.RetCode;
import com.iquantex.samples.account.coreapi.event.AccountQueryEvent;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 账户聚合根实体
 *
 * @author yanliang
 * @date 2020/3/10 16:33
 */
@EntityAggregateAnnotation(aggregateRootType = "BankAccount")
@Getter
@Setter
public class BankAccountAggregate implements Serializable {

	private static final long serialVersionUID = -1L;

	/** 账户编码 */
	private String account;

	/** 账户余额 初始1000 */
	private double balanceAmt = 1000;

	/** 成功转出次数 */
	private int successTransferOut;

	/** 失败转出次数 */
	private int failTransferOut;

	/** 成功转入次数 */
	private int successTransferIn;

	/**
	 * 处理账户划拨命令
	 * @param cmd 划拨指令
	 * @return
	 */
	@CommandHandler(aggregateRootId = "accountCode")
	public ActReturn act(AccountAllocateCmd cmd) {

		if (balanceAmt + cmd.getAmt() < 0) {
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
	 * 处理账户划拨成功事件
	 * @param event 划拨成功事件
	 */
	public void on(AccountAllocateOkEvent event) {
		this.account = event.getAccountCode();
		this.balanceAmt += event.getAmt();
		if (event.getAmt() < 0) {
			this.successTransferOut++;
		}
		else {
			this.successTransferIn++;
		}
	}

	/**
	 * 处理账户划拨失败事件
	 * @param event 划拨失败事件
	 */
	public void on(AccountAllocateFailEvent event) {
		this.account = event.getAccountCode();
		failTransferOut++;
	}

}
