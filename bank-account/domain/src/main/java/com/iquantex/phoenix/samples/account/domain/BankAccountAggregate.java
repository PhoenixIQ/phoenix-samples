package com.iquantex.phoenix.samples.account.domain;

import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.samples.account.api.command.AccountHeartbeatCmd;
import com.iquantex.phoenix.samples.account.api.event.AccountQueryEvent;
import com.iquantex.phoenix.samples.account.api.protobuf.Account;
import com.iquantex.phoenix.server.aggregate.ActReturn;
import com.iquantex.phoenix.server.aggregate.CommandHandler;
import com.iquantex.phoenix.server.aggregate.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.EntityAggregateContext;
import com.iquantex.phoenix.server.aggregate.cls.Inject;
import com.iquantex.phoenix.server.eventstore.snapshot.SelectiveSnapshot;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * * Created by lan on 2019/10/10
 */
@EntityAggregateAnnotation(aggregateRootType = "BankAccount", idempotentSize = 100, bloomSize = 100000)
@Getter
@Setter
@Slf4j
public class BankAccountAggregate implements SelectiveSnapshot<SnapshotData>, BankAccountQueryBehavior {

	@Inject
	private transient BankAccountAllocateBehavior businessObj = new BankAccountAllocateBehavior();

	@Inject
	private transient BankAccountHeartbeatBehavior heartbeatBehavior = new BankAccountHeartbeatBehavior();

	@Value("${bank-account.heartbeat:false}")
	private boolean enabledHeartbeat;

	@CommandHandler(aggregateRootId = "accountCode", isCommandSourcing = true)
	public ActReturn act(Account.AccountCreateCmd createCmd) {
		this.businessObj.setAccount(createCmd.getAccountCode());
		this.businessObj.setBalanceAmt(createCmd.getBalanceAmt());

		String message = String.format("初始化账户代码<%s>, 初始化余额<%s>. ", createCmd.getAccountCode(),
				createCmd.getBalanceAmt());

		// 如果开启了，那么就 10秒一次打印
		if (enabledHeartbeat) {
			EntityAggregateContext.getTimer().startTimerWithFixedDelay(AccountHeartbeatCmd.class.getSimpleName(),
					new AccountHeartbeatCmd(this.businessObj.getAccount()), Duration.ofSeconds(10));
		}

		return ActReturn
				.builder().retCode(RetCode.SUCCESS).retMessage(message).event(Account.AccountCreateEvent.newBuilder()
						.setAccountCode(createCmd.getAccountCode()).setBalanceAmt(createCmd.getBalanceAmt()).build())
				.build();
	}

	@Override
	public AccountQueryEvent genQueryEvent() {
		return AccountQueryEvent.builder().account(this.businessObj.getAccount())
				.balanceAmt(this.businessObj.getBalanceAmt())
				.successTransferOut(this.businessObj.getSuccessTransferOut())
				.failTransferOut(this.businessObj.getFailTransferOut())
				.successTransferIn(this.businessObj.getSuccessTransferIn()).build();
	}

	/**
	 * 自定义序列化数据(快照) 加载动作.
	 * @param snapshotData
	 */
	@Override
	public void loadSnapshot(SnapshotData snapshotData) {
		this.businessObj.setAccount(snapshotData.getAccount());
		this.businessObj.setBalanceAmt(snapshotData.getBalanceAmt());
		this.businessObj.setFailTransferOut(snapshotData.getFailTransferOut());
		this.businessObj.setSuccessTransferIn(snapshotData.getSuccessTransferIn());
		this.businessObj.setSuccessTransferOut(snapshotData.getSuccessTransferOut());
	}

	/**
	 * 自定义序列化数据(快照) , Console 前端展示动作
	 */
	@Override
	public SnapshotData getSnapshot() {
		return saveSnapshot();
	}

	/**
	 * 自定义序列化数据(快照) , 存储动作.
	 */
	@Override
	public SnapshotData saveSnapshot() {
		return SnapshotData.builder().account(this.businessObj.getAccount())
				.balanceAmt(this.businessObj.getBalanceAmt()).successTransferIn(this.businessObj.getSuccessTransferIn())
				.failTransferOut(this.businessObj.getFailTransferOut())
				.successTransferOut(this.businessObj.getSuccessTransferOut()).build();
	}

}
