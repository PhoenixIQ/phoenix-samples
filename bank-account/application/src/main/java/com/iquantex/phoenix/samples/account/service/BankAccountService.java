package com.iquantex.phoenix.samples.account.service;

/**
 * @author baozi
 * @date 2020/6/9 10:58 AM
 */
public interface BankAccountService {

    /**
     * 查询所有账户
     * @param linearizability 查询账户信息,如果linearizability == true,会使用QueryCmd直接读取内存 达到线性一致性读.
     * 否则读业务数据库实现最终一致性读(业务数据库是异步写入的)
     * @return
     */
    String queryAllAccount(boolean linearizability);

    /**
     * 随机批量转账
     *
     * @param total
     * @param tps
     * @param aggregateNum
     * @return
     */
    String startBenchmarkTransfer(int total, int tps, int aggregateNum);

    /**
     * 定向转账
     * @param outAccountCode
     * @param inAccountCode
     * @param amt
     * @return
     */
    String transfer(String outAccountCode, String inAccountCode, double amt);

    /**
     * 随机批量划拨
     * @param total
     * @param tps
     * @param aggregateNum
     * @return
     */
    String batchAllocate(int total, int tps, int aggregateNum);

    /**
     * 定向划拨
     * @param account
     * @param amt
     * @return
     */
    String allocate(String account, double amt, String allocateNumber);

    /**
     * 创建账户聚合根
     * @param account 账户代码
     * @param amt 金额
     * @return result message
     */
    String createAccount(String account, double amt);

    /**
     * 批量创建账户聚合根
     * @param aggregateNum 账户代码
     * @param amt 金额
     * @return result message
     */
    String batchCreateAccount(int aggregateNum, double amt);

}
