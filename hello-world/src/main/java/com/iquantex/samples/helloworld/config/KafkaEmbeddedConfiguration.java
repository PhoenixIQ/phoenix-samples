package com.iquantex.samples.helloworld.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

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
	public EmbeddedKafkaBroker kafkaEmbedded() {
		// broker的数量，由于是本地环境，设置为1就可以
		int brokerServerCnt = 1;
		Map<String, String> brokerProp = new HashMap<>(2);
		// 自动创建topic
		brokerProp.put("auto.create.topics.enable", "true");
		// 默认给每个topic创建4个partition
		brokerProp.put("num.partitions", "4");
		EmbeddedKafkaBroker embeddedKafkaRule = new EmbeddedKafkaBroker(brokerServerCnt).brokerProperties(brokerProp);
		embeddedKafkaRule.kafkaPorts(9092);
		return embeddedKafkaRule;
	}

}
