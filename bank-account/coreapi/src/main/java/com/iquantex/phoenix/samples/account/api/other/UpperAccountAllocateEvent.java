package com.iquantex.phoenix.samples.account.api.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author baozi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpperAccountAllocateEvent {

	/** 账户标签 */
	private String accountTag;

	/** 初始化金额 */
	private double amt;

}
