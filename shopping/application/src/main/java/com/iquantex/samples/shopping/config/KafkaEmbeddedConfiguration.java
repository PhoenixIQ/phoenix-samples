package com.iquantex.samples.shopping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.rule.KafkaEmbedded;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: baozi
 * @Date: 2020/8/20 12:15
 */
@Configuration
public class KafkaEmbeddedConfiguration {

	/**
	 * 开启一个内存kafka，端口随机
	 * @return
	 */
	@Bean
	public KafkaEmbedded kafkaEmbedded() {
		int brokerServerCnt = 1; // broker的数量，由于是本地环境，设置为1就可以
		Map<String, String> brokerProp = new HashMap<>();
		brokerProp.put("auto.create.topics.enable", "true"); // 自动创建topic
		brokerProp.put("num.partitions", "4"); // 默认给每个topic创建4个partition
		KafkaEmbedded kafkaEmbedded = new KafkaEmbedded(brokerServerCnt).brokerProperties(brokerProp);
		kafkaEmbedded.setKafkaPorts(9092);
		return kafkaEmbedded;
	}

}
