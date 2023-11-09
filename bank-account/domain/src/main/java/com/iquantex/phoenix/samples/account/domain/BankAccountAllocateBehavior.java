package com.iquantex.phoenix.samples.account.domain;

import com.google.common.util.concurrent.Uninterruptibles;
import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.core.util.StringUtils;
import com.iquantex.phoenix.samples.account.api.command.AccountAllocateCmd;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateFailEvent;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateOkEvent;
import com.iquantex.phoenix.samples.account.api.protobuf.Account;
import com.iquantex.phoenix.samples.account.domain.subcase.BroadcastCase;
import com.iquantex.phoenix.samples.account.domain.subcase.CpuBoundCase;
import com.iquantex.phoenix.server.aggregate.ActReturn;
import com.iquantex.phoenix.server.aggregate.CommandHandler;
import com.iquantex.phoenix.server.aggregate.EntityAggregateContext;
import com.iquantex.phoenix.server.aggregate.RegistryCollectData;
import com.iquantex.phoenix.server.aggregate.cls.AggregateSegment;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 片段代码，实现划拨，聚合根的主要逻辑在这里.
 */
@Slf4j
@AggregateSegment
@Data
public class BankAccountAllocateBehavior {

	private static final String MONITORY_TIME_OUT_ACCOUNT = "monitor_retry";

	/**
	 * 账户代码
	 */
	private String account;

	/**
	 * 账户余额
	 */
	private double balanceAmt;

	/**
	 * 成功转出次数
	 */
	private int successTransferOut;

	/**
	 * 失败转出次数
	 */
	private int failTransferOut;

	/**
	 * 成功转入次数
	 */
	private int successTransferIn;

	@Value("${mock-latency:1}")
	private Long mockLatency = 1L;

	/**
	 * 是否允许请求通过
	 */
	@Autowired
	private transient MockService mockService;

	private transient CpuBoundCase cpuBoundCase = new CpuBoundCase();

	private transient BroadcastCase broadcastCase = new BroadcastCase();

	@Value("${eq10.subscribe.address}")
	private String mqAddress;

	@Value("${eq10.subscribe.topic}")
	private String subscribeTopic;

	@Value("${bank-account.cpu-bound:false}")
	private boolean enabledCpuBound;

	/**
	 * 处理账户划拨命令
	 * @param cmd
	 * @return
	 */
	@CommandHandler(aggregateRootId = "accountCode",
			idempotentIds = { "AccountAllocateCmd", "accountCode", "allocateNumber" })
	public ActReturn act(AccountAllocateCmd cmd) {
		this.setAccount(cmd.getAccountCode());

		// 模拟处理超时堵塞65s
		if (cmd.getAccountCode().equals(MONITORY_TIME_OUT_ACCOUNT)) {
			Uninterruptibles.sleepUninterruptibly(65, TimeUnit.SECONDS);
		}

		// 模拟 CPU 密集型操作
		if (enabledCpuBound) {
			cpuBoundCase.workload();
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
			RegistryCollectData broadcastMetaData = broadcastCase.getCollectMetaData(balanceAmt, cmd.getAmt(),
					mqAddress, subscribeTopic, account);

			String retMessage = String.format("账户划拨成功：划拨金额：%.2f，账户余额：%.2f", cmd.getAmt(), balanceAmt + cmd.getAmt());
			return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage(retMessage)
					.registryCollectData(broadcastMetaData) // 返回 null 也没问题
					.event(new AccountAllocateOkEvent(cmd.getAccountCode(), cmd.getAmt())).build();
		}
	}

	/**
	 * 处理账户划拨成功事件
	 * @param event
	 */
	public void on(AccountAllocateOkEvent event) {
		account = event.getAccountCode();
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
