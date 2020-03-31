package com.iquantex.phoenix.application.controller;

import com.iquantex.phoenix.application.utils.RateLimiter;
import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.client.RpcResult;
import com.iquantex.phoenix.coreapi.AccountAllocateCmd;
import com.iquantex.phoenix.domain.entity.BankAccountAggregate;
import com.iquantex.phoenix.server.controller.AggregateController;
import com.iquantex.phoenix.server.eventsourcing.AggregateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 银行转账controller类
 *
 * @author yanliang
 * @date 2/20/2020 2:44
 */
@Slf4j
@RestController
@RequestMapping("/accounts")
public class BankAccountController extends AggregateController {

	/** phoenix 客户端 */
	@Autowired
	private PhoenixClient client;

	/**
	 * 账户总览
	 * @return 账户信息
	 */
	@GetMapping("")
	public String accounts() {
		int pageIndex = 1;
		int pageSize = 1000;
		List<BankAccountAggregate> aggList = new ArrayList<>();
		while (true) {
			// 查询聚合根列表
			List<String> tmpList = AggregateRepository.getInstance()
					.getAggregateIdListByAggregateRootType("BankAccount", pageIndex, pageSize);
			log.info("select all aggregate:{}", tmpList);
			if (tmpList.isEmpty()) {
				break;
			}
			// 组装账户信息
			for (String aggId : tmpList) {
				BankAccountAggregate aggregate = (BankAccountAggregate) AggregateRepository.getInstance().load(aggId)
						.getAggregateRoot();
				aggregate.setAccount(aggId);
				aggList.add(aggregate);
			}
			pageIndex++;
		}
		return showAsHTML(aggList);
	}

	/**
	 * 将结果显示为Html格式
	 * @param aggList 账户信息
	 * @return 输出html结果
	 */
	private String showAsHTML(List<BankAccountAggregate> aggList) {
		double totalBalanceAmt = 0;
		int totalSuccessTransferOut = 0;
		int totalFailTransferOut = 0;
		int totalSuccessTransferIn = 0;
		StringBuilder buffer = new StringBuilder();
		buffer.append("<table border=1 width='1200px'>");
		buffer.append("<tr><th>银行账户</th><th>账户余额</th><th>成功转出次数</th><th>失败转出次数</th><th>成功转入次数</th></tr>");
		for (BankAccountAggregate aggregate : aggList) {
			buffer.append("<tr>");
			// 由于聚合根id已经拼接类名，为了显示美观，这里截取一下。
			buffer.append("<td>").append(aggregate.getAccount().split("@")[2]).append("</td>");
			buffer.append("<td>").append(aggregate.getBalanceAmt()).append("</td>");
			buffer.append("<td>").append(aggregate.getSuccessTransferOut()).append("</td>");
			buffer.append("<td>").append(aggregate.getFailTransferOut()).append("</td>");
			buffer.append("<td>").append(aggregate.getSuccessTransferIn()).append("</td>");
			buffer.append("</tr>");
			totalBalanceAmt += aggregate.getBalanceAmt();
			totalSuccessTransferOut += aggregate.getSuccessTransferOut();
			totalFailTransferOut += aggregate.getFailTransferOut();
			totalSuccessTransferIn += aggregate.getSuccessTransferIn();
		}
		if (aggList.size() > 1) {
			buffer.append("<tr>");
			buffer.append("<td>账户数量:").append(aggList.size()).append("</td>");
			buffer.append("<td>账户余额汇总:").append(totalBalanceAmt).append("</td>");
			buffer.append("<td>成功转出汇总：").append(totalSuccessTransferOut).append("</td>");
			buffer.append("<td>失败转出汇总：").append(totalFailTransferOut).append("</td>");
			buffer.append("<td>成功转入汇总：").append(totalSuccessTransferIn).append("</td>");
			buffer.append("</tr>");
		}
		buffer.append("</table>");
		return buffer.toString();
	}

	/**
	 * 随机划拨
	 * @param total 划拨总数
	 * @param tps 每秒划拨数量
	 * @param aggregateNum 账户数量
	 * @return 请求结果
	 */
	@GetMapping("/allocate/pf/{total}/{tps}/{aggregateNum}")
	public String startMessageTest(@PathVariable int total, @PathVariable int tps, @PathVariable int aggregateNum) {
		// 参数校验
		if (tps <= 0) {
			return "tps cannot be less than 1";
		}
		if (total <= 0) {
			return "total cannot be less than 1";
		}
		if (aggregateNum < 1) {
			return "aggregateNum cannot be less than 1";
		}
		log.info("start a message test: aggregateNum<{}> total<{}>, tps<{}>", aggregateNum, total, tps);
		int timeout = total / tps + 5;
		// 设置速率限制器发送请求
		RateLimiter messageTestTask = new RateLimiter(tps, total, timeout, () -> {
			String account = String.format("A%08d", new Random().nextInt(aggregateNum));
			int amt = (100 - new Random().nextInt(200));
			AccountAllocateCmd cmd = new AccountAllocateCmd(account, amt);
			client.send(cmd, "");
		}, "account", new RateLimiter.RunMonitor(false));
		// 开始随机划拨
		messageTestTask.start();
		return String.format("开始随机转账:total=<%d>,tps=<%d>,aggregateNum=<%d>", total, tps, aggregateNum);
	}

	/**
	 * 定向划拨
	 * @param account 账户
	 * @param amt 划拨金额
	 * @return 划拨结果
	 */
	@PutMapping("/allocate/{account}/{amt}")
	public String allocate(@PathVariable String account, @PathVariable double amt) {
		AccountAllocateCmd cmd = new AccountAllocateCmd(account, amt);
		Future<RpcResult> future = client.send(cmd, "");
		try {
			RpcResult result = future.get(10, TimeUnit.SECONDS);
			return result.getMessage();
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			return "rpc error: " + e.getMessage();
		}
	}

}
