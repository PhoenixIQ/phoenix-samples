package com.iquantex.account.service;

import com.iquantex.samples.account.coreapi.other.UpperAccountTransEvent;

/**
 * @author baozi
 * @date 2020/6/9 10:58 AM
 */
public interface BankAccountService {

	/**
	 * 定向转账
	 * @param accountTransEvent
	 * @return
	 */
	boolean transAccount(UpperAccountTransEvent accountTransEvent);

	/**
	 * 查询所有账户
	 * @param linearizability
	 *
	 * 查询账户信息,如果linearizability == true,会使用QueryCmd直接读取内存 达到线性一致性读.
	 * 否则读业务数据库实现最终一致性读(业务数据库是异步写入的)
	 * @return
	 */
	String queryAllAccount(boolean linearizability);

	/**
	 * 批量划拨
	 * @param total
	 * @param tps
	 * @param aggregateNum
	 * @return
	 */
	String batchAllocate(int total, int tps, int aggregateNum);

	/**
	 * 划拨
	 * @param account
	 * @param amt
	 * @return
	 */
	String allocate(String account, double amt);

}
