package com.iquantex.samples.account.coreapi.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 账户转账命令
 *
 * @author baozi
 * @date 2019/12/12 3:52 下午
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferCmd implements Serializable {

	private static final long serialVersionUID = -8951705463515914994L;

	/** 转入账户 */
	private String inAccountCode;

	/** 转出账户 */
	private String outAccountCode;

	/** 转入金额 */
	private double amt;

}
