package com.iquantex.phoenix.samples.account.listener;

import com.iquantex.phoenix.core.connect.api.CollectMetaDataQuery;
import com.iquantex.phoenix.core.connect.api.CollectResult;
import com.iquantex.phoenix.core.connect.api.Records;
import com.iquantex.phoenix.core.connect.api.SourceCollect;
import com.iquantex.phoenix.core.connect.api.Subscribe;
import com.iquantex.phoenix.core.connect.kafka.KafkaSubscribe;
import com.iquantex.phoenix.core.util.JsonUtil;
import com.iquantex.phoenix.core.util.UUIDUtils;
import com.iquantex.phoenix.samples.account.api.command.AccountAllocateCmd;
import com.iquantex.phoenix.samples.account.api.other.UpperAccountAllocateEvent;
import com.iquantex.phoenix.samples.account.api.protobuf.Account;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订阅创建账户的事件，将这类mq消息序列化成事件
 *
 * @author liulin
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "eq10.subscribe.enabled", havingValue = "true")
public class Eq10AllocateEventSubscribe {

	@Value("${spring.application.name}")
	private String appName;

	@Value("${eq10.subscribe.address}")
	private String mqAddress;

	@Value("${eq10.subscribe.topic}")
	private String subscribeTopic;

	@Bean
	public Subscribe broadcastEventSubscribe() {
		Properties properties = new Properties();
		properties.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		return new KafkaSubscribe(mqAddress, subscribeTopic, appName, properties, new SelfDeseriSchema());
	}

	class SelfDeseriSchema implements SourceCollect {

		@Override
		public List<CollectResult> collect(Records records, CollectMetaDataQuery collectMetaData) {
			List<CollectResult> collectResults = new ArrayList<>();
			// 外部批量聚合根事件转换为多个聚合根的创建cmd
			if (UpperAccountAllocateEvent.class.getName().equals(records.getKey())) {
				UpperAccountAllocateEvent upperAccountAllocateEvent = JsonUtil.decode(new String(records.getValue()),
						records.getKey());
				Set<String> amtEQ10 = collectMetaData.get(upperAccountAllocateEvent.getAccountTag());
				log.info("subscribe tag {} values <{}>", upperAccountAllocateEvent.getAccountTag(), amtEQ10);
				if (amtEQ10 != null && !amtEQ10.isEmpty()) {
					for (String value : amtEQ10) {
						AccountAllocateCmd accountAllocateCmd = new AccountAllocateCmd(value,
								upperAccountAllocateEvent.getAmt(), UUIDUtils.genUUID());
						collectResults.add(new CollectResult(accountAllocateCmd, true));
					}
				}
			}
			return collectResults;
		}

	}

}
