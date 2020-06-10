package com.iquantex.samples.account.coreapi.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 账户划拨命令
 *
 * @author yanliang
 * @date 2020/3/10 16:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountAllocateCmd implements Serializable {

	private static final long serialVersionUID = -6057211815322013504L;

	/** 划拨账户 */
	private String accountCode;

	/** 划拨金额,允许正负 */
	private double amt;

}
