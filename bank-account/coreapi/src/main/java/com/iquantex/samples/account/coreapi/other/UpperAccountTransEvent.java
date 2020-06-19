package com.iquantex.samples.account.coreapi.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模拟上游转账事件
 *
 * @author baozi
 * @date 2020/5/27 10:38 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpperAccountTransEvent {

	/**
	 * 转出账户
	 */
	private String outAccountCode;

	/**
	 * 转入账户
	 */
	private String inAccountCode;

	/**
	 * 转入金额(正)
	 */
	private double amt;

}
