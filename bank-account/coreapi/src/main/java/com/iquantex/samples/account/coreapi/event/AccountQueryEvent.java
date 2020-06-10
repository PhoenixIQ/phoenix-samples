package com.iquantex.samples.account.coreapi.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 账户查询事件
 *
 * @author baozi
 * @date 2020/3/26 8:11 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountQueryEvent implements Serializable {

	private static final long serialVersionUID = 6451409645916161516L;

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
