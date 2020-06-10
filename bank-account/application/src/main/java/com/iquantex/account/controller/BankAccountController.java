package com.iquantex.account.controller;

import com.iquantex.account.service.BankAccountService;
import com.iquantex.phoenix.server.controller.AggregateController;
import com.iquantex.samples.account.coreapi.other.UpperAccountTransEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@Autowired
	private BankAccountService bankAccountService;

	/**
	 * 账户总览
	 * @return 账户信息
	 */
	@GetMapping("/{linearizability}")
	public String accounts(@PathVariable int linearizability) {
		return bankAccountService.queryAllAccount(linearizability == 1 ? true : false);
	}

	/**
	 * 定向转账
	 * @param outAccountCode
	 * @param inAccountCode
	 * @param amt
	 * @return
	 */
	@PutMapping("/transfers/{outAccountCode}/{inAccountCode}/{amt}")
	public String transfer(@PathVariable String outAccountCode, @PathVariable String inAccountCode,
			@PathVariable double amt) {
		bankAccountService.transAccount(new UpperAccountTransEvent(outAccountCode, inAccountCode, amt));
		return "trans send ok";
	}

	/**
	 * 定向划拨
	 * @param account 账户
	 * @param amt 划拨金额
	 * @return 划拨结果
	 */
	@PutMapping("/allocate/{account}/{amt}")
	public String allocate(@PathVariable String account, @PathVariable double amt) {
		return bankAccountService.allocate(account, amt);
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
		return bankAccountService.batchAllocate(total, tps, aggregateNum);

	}

}
