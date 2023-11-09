package com.iquantex.phoenix.samples.account.domain.subcase;

import com.iquantex.phoenix.server.aggregate.EntityAggregateContext;
import java.io.Serializable;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** CPU 密集型计算案例 */
@Slf4j
public class CpuBoundCase {

	private static final int AVAILABLE_PROCESSOR = Runtime.getRuntime().availableProcessors();

	public void workload() {
		// 返回的结果是大于状态的
		// 序列化状态时，尽量在原端序列化一次.
		// 支持隔离，例如 N 个用于计算， N 个用于实体聚合根.
		List<Serializable> result = EntityAggregateContext.executeOnPipeline(pipeline -> {
			// 核心数量的 8 倍任务
			for (int i = 0; i < AVAILABLE_PROCESSOR << 3; i++) {
				// 模拟 50ms 的计算耗时.
				pipeline.submit(FactorialNum.THREE, Factorial.class);
			}
		});

		log.info("计算结果大小: {}", result.size());
	}

}
