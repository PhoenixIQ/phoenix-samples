package com.iquantex.samples.account.coreapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 账户划拨成功事件
 *
 * @author yanliang
 * @date 2020/3/10 16:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountAllocateOkEvent implements Serializable {

	/** 划拨账户 */
	private String accountCode;

	/** 划拨金额 */
	private double amt;

}
