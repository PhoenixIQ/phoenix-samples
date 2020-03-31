package com.iquantex.phoenix.coreapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2020/3/25 3:04 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCmd implements Serializable {

	/** 划拨账户 */
	private String accountCode;

	/** 划拨金额,允许正负 */
	private double amt;

}
