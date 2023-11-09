package com.iquantex.phoenix.samples.account.bean;

import java.util.HashMap;
import java.util.Map;

import com.iquantex.phoenix.samples.account.listener.BankAccountEventListener;
import com.iquantex.phoenix.starter.autoconfigrue.properties.PhoenixClientProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

@Slf4j
@EnableKafka
@Configuration
@ConditionalOnProperty(value = "event.listener.enabled", havingValue = "true")
public class KafkaConsumerConfiguration {

	@Value("${spring.kafka.bootstrap-servers}")
	private String mqAddress;

	/**
	 * 创建一个kafka的Consumer工厂
	 * @return
	 */
	@Bean
	public ConsumerFactory<String, byte[]> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, mqAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "account-pub-listener");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
		return new DefaultKafkaConsumerFactory<>(props);
	}

	/**
	 * 创建一个Kafka的listener
	 * @return
	 */
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerContainerFactory(
			ConsumerFactory<String, byte[]> consumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		factory.setBatchListener(true);
		factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
		return factory;
	}

	/**
	 * 创建账户事件的listener
	 * @return
	 */
	@Bean
	public BankAccountEventListener bankAccountListener() {
		log.info("start bank account event listener");
		return new BankAccountEventListener();
	}

}
