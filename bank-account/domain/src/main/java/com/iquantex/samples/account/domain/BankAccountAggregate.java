package com.iquantex.samples.account.domain;

import com.iquantex.samples.account.coreapi.AccountAllocateCmd;
import com.iquantex.samples.account.coreapi.AccountAllocateFailEvent;
import com.iquantex.samples.account.coreapi.AccountAllocateOkEvent;
import com.iquantex.phoenix.server.aggregate.entity.CommandHandler;
import com.iquantex.phoenix.server.aggregate.entity.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.model.ActReturn;
import com.iquantex.phoenix.server.aggregate.model.RetCode;
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

	// 核心业务数据
	private String account; // 账户代码

	private double balanceAmt; // 账户余额

	// 辅助统计数据
	private int successTransferOut; // 成功转出次数

	private int failTransferOut; // 失败转出次数

	private int successTransferIn; // 成功转入次数

	public BankAccountAggregate() {
		this.balanceAmt = 1000;
	}

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
	 * 处理账户划拨成功事件
	 * @param event 划拨成功事件
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
	 * @param event 划拨失败事件
	 */
	public void on(AccountAllocateFailEvent event) {
		failTransferOut++;
	}

}
