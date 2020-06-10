package com.iquantex.samples.account.coreapi.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 账户划拨失败事件
 *
 * @author yanliang
 * @date 2020/3/10 16:41
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountAllocateFailEvent implements Serializable {

	private static final long serialVersionUID = 1799460299651156857L;

	/** 划拨账户 */
	private String accountCode;

	/** 划拨金额 */
	private double amt;

	/** 失败原因 */
	private String result;

}
