package com.iquantex.phoenix.samples.account.domain.subcase;

import java.io.Serializable;
import lombok.Data;

/** 模拟用户状态是 10M 的状态. */
@Data
public class FactorialNum implements Serializable {

	public static final int SIZE_KB = 1024;

	public static final int SIZE_MB = 1024 * 1024;

	public static final int MILLION = 1000_000;

	public static final int THOUSAND = 1000;

	public static final FactorialNum ONE = new FactorialNum(1, SIZE_MB);

	public static final FactorialNum TWO = new FactorialNum(1, 5 * SIZE_MB);

	public static final FactorialNum THREE = new FactorialNum(33 * THOUSAND, SIZE_MB);

	public static final FactorialNum FORTH = new FactorialNum(33 * THOUSAND, 10 * SIZE_MB);

	private static final long serialVersionUID = 1918505094872820541L;

	private final int num;

	private final byte[] payload;

	public FactorialNum(int num, int size) {
		this.num = num;
		this.payload = new byte[size];
	}

}
