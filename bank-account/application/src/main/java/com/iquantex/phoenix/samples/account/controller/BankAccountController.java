package com.iquantex.phoenix.samples.account.controller;

import org.springframework.web.bind.annotation.*;

import com.iquantex.phoenix.samples.account.service.BankAccountService;
import com.iquantex.phoenix.starter.autoconfigrue.controller.BaseController;

import lombok.extern.slf4j.Slf4j;

/**
 * 银行转账controller类
 *
 * @author yanliang
 * @date 2/20/2020 2:44
 */
@Slf4j
@RestController
@RequestMapping("/accounts")
public class BankAccountController extends BaseController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    /**
     * 账户总览
     * @return 账户信息
     */
    @GetMapping("/{linearizability}")
    public String accounts(@PathVariable int linearizability) {
        return bankAccountService.queryAllAccount(linearizability == 1 ? true : false);
    }

    /**
     * 随机批量转账
     * @param total
     * @param tps
     * @param aggregateNum
     * @return
     */
    @GetMapping("/transfers/pf/{total}/{tps}/{aggregateNum}")
    public String startBenchmarkTransfer(@PathVariable int total, @PathVariable int tps, @PathVariable int aggregateNum) {
        if (tps <= 0) {
            return "tps cannot be less than 1";
        }
        if (total <= 0) {
            return "total cannot be less than 1";
        }
        if (aggregateNum < 1) {
            return "aggregateNum cannot be less than 1";
        }
        return bankAccountService.startBenchmarkTransfer(total, tps, aggregateNum);
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
        if (amt < 0) {
            return "amt is not < 0: " + amt;
        }
        return bankAccountService.transfer(outAccountCode, inAccountCode, amt);
    }

    /**
     * 随机批量划拨
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

    /**
     * 定向划拨
     * @param account 账户
     * @param amt 划拨金额
     * @return 划拨结果
     */
    @PutMapping("/allocate/{account}/{amt}/{allocateNumber}")
    public String allocate(@PathVariable String account, @PathVariable double amt, @PathVariable String allocateNumber) {
        return bankAccountService.allocate(account, amt, allocateNumber);
    }

    /**
     * 创建账户聚合根
     * @param account 账户代码
     * @param amt 金额
     * @return result message
     */
    @PutMapping("/create/{account}/{amt}")
    public String init(@PathVariable String account, @PathVariable double amt) {
        return bankAccountService.createAccount(account, amt);
    }

    /**
     * 批量创建账户聚合根
     * @param aggregateNum 账户代码
     * @param amt 金额
     * @return result message
     */
    @PutMapping("/batch_create/{aggregateNum}/{amt}")
    public String batchInit(@PathVariable int aggregateNum, @PathVariable double amt) {
        return bankAccountService.batchCreateAccount(aggregateNum, amt);
    }

}
