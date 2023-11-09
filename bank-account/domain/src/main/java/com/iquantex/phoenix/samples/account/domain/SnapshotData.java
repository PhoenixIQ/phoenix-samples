package com.iquantex.phoenix.samples.account.domain;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * 快照中的数据接口（自定义）,用户需要快照中存储哪些数据就定义哪些
 *
 * @author quail
 */
@Data
@Builder
public class SnapshotData implements Serializable {

	private static final long serialVersionUID = -5908472083137878870L;

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
