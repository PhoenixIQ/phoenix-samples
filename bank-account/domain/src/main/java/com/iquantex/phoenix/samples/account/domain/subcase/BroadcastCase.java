package com.iquantex.phoenix.samples.account.domain.subcase;

import com.iquantex.phoenix.core.connect.kafka.KafkaSubscribe;
import com.iquantex.phoenix.server.aggregate.RegistryCollectData;
import java.util.Arrays;

/** 广播案例 */
public class BroadcastCase {

	public RegistryCollectData getCollectMetaData(double balanceAmt, double amt, String mqAddress,
			String subscribeTopic, String account) {
		if (balanceAmt + amt == 10.0) {
			return new RegistryCollectData(RegistryCollectData.Type.REGISTRY,
					KafkaSubscribe.genSplitRangeId(mqAddress, subscribeTopic), Arrays.asList("amtEQ10"), account);
		}

		// 取消订阅
		if (balanceAmt == 10.0 && balanceAmt + amt != 10) {
			return new RegistryCollectData(RegistryCollectData.Type.UN_REGISTRY,
					KafkaSubscribe.genSplitRangeId(mqAddress, subscribeTopic), Arrays.asList("amtEQ10"), account);
		}
		return null;
	}

}
