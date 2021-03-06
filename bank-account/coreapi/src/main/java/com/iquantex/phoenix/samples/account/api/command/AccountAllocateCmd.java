package com.iquantex.phoenix.samples.account.api.command;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author baozi
 * @date 2019/12/12 3:44 下午
 * @Description 账户划拨命令
 */
@Data
@NoArgsConstructor
public class AccountAllocateCmd implements Serializable {

	public AccountAllocateCmd(String accountCode, double amt) {
		this.accountCode = accountCode;
		this.amt = amt;
		this.allocateNumber = UUID.randomUUID().toString();
	}

	public AccountAllocateCmd(String accountCode, double amt, String allocateNumber) {
		this.accountCode = accountCode;
		this.amt = amt;
		this.allocateNumber = allocateNumber;
		if (allocateNumber == null || allocateNumber.length() == 0) {
			this.allocateNumber = UUID.randomUUID().toString();
		}
	}

	private String accountCode; // 划拨账户

	private double amt; // 划拨金额,允许正负

	private String allocateNumber; // 划拨编号

}
