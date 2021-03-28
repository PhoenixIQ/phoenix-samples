package com.iquantex.samples.shopping.coreapi.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author baozi
 */
@ToString
@AllArgsConstructor
@Getter
public class AccountQueryEvent implements Serializable {

	private static final long serialVersionUID = -4177381326154554282L;

	/** 账户 */
	private String accountCode;

	/** 余额 */
	private double amt;

	/** 冻结金额 */
	private double frozenAmt;

}
