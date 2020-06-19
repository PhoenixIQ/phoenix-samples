package com.iquantex.samples.account.domain;

import com.iquantex.phoenix.transaction.aggregate.transaction.SagaAction;
import com.iquantex.phoenix.transaction.aggregate.transaction.TransactionAggregateAnnotation;
import com.iquantex.phoenix.transaction.aggregate.transaction.TransactionFinishReturn;
import com.iquantex.phoenix.transaction.aggregate.transaction.TransactionReturn;
import com.iquantex.phoenix.transaction.aggregate.transaction.TransactionStart;
import com.iquantex.samples.account.coreapi.command.AccountAllocateCmd;
import com.iquantex.samples.account.coreapi.command.AccountTransferCmd;
import com.iquantex.samples.account.coreapi.event.AccountAllocateFailEvent;
import com.iquantex.samples.account.coreapi.event.AccountAllocateOkEvent;

import java.io.Serializable;

/**
 * 账户聚合根实体
 *
 * @author lan
 * @date 2019/10/10 16:33
 */
@TransactionAggregateAnnotation(aggregateRootType = "BankTransferSaga")
public class BankTransferSaga implements Serializable {

	private static final long serialVersionUID = 2834866334398657786L;

	/** 事务返回内容 */
	private String retMessage = "";

	/** 事务请求命令 */
	private AccountTransferCmd transferCmd;

	/**
	 * 处理转账请求,先发起转出
	 * @param transferCmd
	 * @return
	 */
	@TransactionStart
	public TransactionReturn on(AccountTransferCmd transferCmd) {
		this.transferCmd = transferCmd;
		return TransactionReturn.builder().addAction(
				// saga事务
				new SagaAction(
						// 转出账户扣钱
						new AccountAllocateCmd(transferCmd.getOutAccountCode(), -transferCmd.getAmt()),
						// 转出账户失败回滚(加钱)
						null))
				.build();
	}

	/**
	 * 账户划拨正常
	 * @param event
	 * @return
	 */
	public TransactionReturn on(AccountAllocateOkEvent event) {
		// 转出正常,请求转入
		if (event.getAmt() < 0) {
			return TransactionReturn.builder()
					.addAction(new SagaAction(
							new AccountAllocateCmd(transferCmd.getInAccountCode(), transferCmd.getAmt()), null))
					.build();
		}
		// 转入正常,转账结束
		else {
			retMessage = String.format("账户转账成功：转出账户：%s，转入账户：%s，转账金额：%s", transferCmd.getOutAccountCode(),
					transferCmd.getInAccountCode(), transferCmd.getAmt());
			return null;
		}
	}

	/**
	 * 账户划拨失败
	 * @param event
	 * @return
	 */
	public TransactionReturn on(AccountAllocateFailEvent event) {
		retMessage = String.format("账户转账失败：转出账户：%s，转入账户：%s，转账金额：%s，失败原因：%s", transferCmd.getOutAccountCode(),
				transferCmd.getInAccountCode(), transferCmd.getAmt(), event.getResult());
		return null;
	}

	/**
	 * 事务结束调用方法
	 * @return
	 */
	public TransactionFinishReturn onFinish() {
		return TransactionFinishReturn.builder().retMessage(retMessage).data("account transfer finish").build();
	}

}
