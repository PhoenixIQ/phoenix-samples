package com.iquantex.samples.account.coreapi.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 账户查询命令
 * @author baozi
 * @date 2020/3/26 8:10 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountQueryCmd implements Serializable {

	private static final long serialVersionUID = -340749663592564226L;

	/** 账户编码 */
	private String accountCode;

}
