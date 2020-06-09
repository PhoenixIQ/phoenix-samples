package com.iquantex.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Builder
@Table(name = "ACCOUNT_STORE")
@AllArgsConstructor
@NoArgsConstructor
public class AccountStore implements Serializable {

	@Id
//	@Column(columnDefinition = "comment '账户编码'")
	private String accountCode;

//	@Column(columnDefinition = "comment '账户余额'")
	private double balanceAmt;

//	@Column(columnDefinition = "comment '成功转出次数'")
	private int successTransferOut;

//	@Column(columnDefinition = "comment '失败转出次数'")
	private int failTransferOut;

//	@Column(columnDefinition = "comment '成功转入次数'")
	private int successTransferIn;

}
