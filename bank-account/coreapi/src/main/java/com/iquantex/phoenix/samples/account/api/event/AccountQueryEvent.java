package com.iquantex.phoenix.samples.account.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2020/3/26 8:11 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountQueryEvent implements Serializable {

	/** 账户代码 */
	private String account;

	/** 账户余额 */
	private double balanceAmt;

	/** 成功转出次数 */
	private int successTransferOut;

	/** 失败转出次数 */
	private int failTransferOut;

	/** 成功转入次数 */
	private int successTransferIn;

}
