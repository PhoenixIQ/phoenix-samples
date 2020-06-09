package com.iquantex.account.config;

import com.iquantex.account.subscribe.BankAccountEventListener;
import com.iquantex.phoenix.starter.autoconfigrue.properties.PhoenixEventPublishProperties;
import lombok.extern.slf4j.Slf4j;
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

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@ConditionalOnProperty(prefix = PhoenixEventPublishProperties.PREFIX, name = "enabled", havingValue = "true")
@Slf4j
public class KafkaConsumerConfiguration {

	@Value("${spring.kafka.bootstrap-servers}")
	private String mqAddress;

	@Bean
	public ConsumerFactory<String, byte[]> consumerFactory() {
		return getConsumerFactory(mqAddress, "bank-account-sub");
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerContainerFactory(
			ConsumerFactory<String, byte[]> consumerFactory) {
		return getContainerFactory(consumerFactory);
	}

	@Bean
	public BankAccountEventListener bankAccountListener() {
		return new BankAccountEventListener();
	}

	private ConcurrentKafkaListenerContainerFactory<String, byte[]> getContainerFactory(
			ConsumerFactory<String, byte[]> consumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		factory.setBatchListener(true);
		factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
		return factory;
	}

	private ConsumerFactory<String, byte[]> getConsumerFactory(String mqAddress, String groupId) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, mqAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
		return new DefaultKafkaConsumerFactory<>(props);
	}

}
