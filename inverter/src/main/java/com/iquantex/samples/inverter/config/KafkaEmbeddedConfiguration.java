package com.iquantex.samples.inverter.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.rule.KafkaEmbedded;

@Configuration
@ConditionalOnProperty(value = "embedded-kafka", havingValue = "true", matchIfMissing = true)
public class KafkaEmbeddedConfiguration {

  /**
   * 开启一个内存kafka，端口随机
   *
   * @return
   */
  @Bean
  public KafkaEmbedded kafkaEmbedded() {
    // broker的数量，由于是本地环境，设置为1就可以
    int brokerServerCnt = 1;
    Map<String, String> brokerProp = new HashMap<>();
    // 自动创建topic
    brokerProp.put("auto.create.topics.enable", "true");
    // 默认给每个topic创建4个partition
    brokerProp.put("num.partitions", "4");
    KafkaEmbedded kafkaEmbedded = new KafkaEmbedded(brokerServerCnt).brokerProperties(brokerProp);
    kafkaEmbedded.setKafkaPorts(9092);
    return kafkaEmbedded;
  }
}
