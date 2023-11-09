package com.iquantex.phoenix.samples.account.domain.subcase;

import com.iquantex.phoenix.server.aggregate.pipe.api.Task;
import java.math.BigInteger;

/** 任务的抽象, 这里的对象是无状态的，仅会在计算前读取状态 */
public class Factorial implements Task<FactorialNum, BigInteger> {

	@Override
	public BigInteger execute(FactorialNum factorialNum) {
		BigInteger factorial = BigInteger.ONE;
		for (int i = 1; i <= factorialNum.getNum(); i++) {
			factorial = factorial.multiply(BigInteger.valueOf(i));
		}
		return factorial;
	}

}
