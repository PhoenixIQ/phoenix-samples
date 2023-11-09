package com.iquantex.phoenix.samples.account.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "ACCOUNT_STORE")
@AllArgsConstructor
@NoArgsConstructor
public class AccountStore implements Serializable {

	@Id
	// @Column(columnDefinition = "comment '账户编码'")
	private String accountCode;

	// @Column(columnDefinition = "comment '账户余额'")
	private double balanceAmt;

	// @Column(columnDefinition = "comment '成功转出次数'")
	private int successTransferOut;

	// @Column(columnDefinition = "comment '失败转出次数'")
	private int failTransferOut;

	// @Column(columnDefinition = "comment '成功转入次数'")
	private int successTransferIn;

}
