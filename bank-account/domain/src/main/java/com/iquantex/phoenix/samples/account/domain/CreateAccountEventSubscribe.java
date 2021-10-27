package com.iquantex.phoenix.samples.account.domain;

import com.iquantex.phoenix.core.connect.api.CollectMetaData;
import com.iquantex.phoenix.core.connect.api.CollectResult;
import com.iquantex.phoenix.core.connect.api.Records;
import com.iquantex.phoenix.core.connect.api.SourceCollect;
import com.iquantex.phoenix.core.connect.api.Subscribe;
import com.iquantex.phoenix.core.connect.kafka.KafkaSubscribe;
import com.iquantex.phoenix.samples.account.api.other.UpperAccountCreateEvent;
import com.iquantex.phoenix.samples.account.api.protobuf.Account.AccountCreateCmd;
import com.iquantex.phoenix.samples.account.utils.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订阅创建账户的事件，将这类mq消息序列化成事件
 *
 * @author AndyChen
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "create-account-event.subscribe.enabled", havingValue = "true")
public class CreateAccountEventSubscribe {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${create-account-event.subscribe.address}")
    private String mqAddress;

    @Value("${create-account-event.subscribe.topic}")
    private String subscribeTopic;

    @Bean
    public Subscribe customSubscribe() {
        Properties properties = new Properties();
        properties.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return new KafkaSubscribe(mqAddress, subscribeTopic, appName, properties,
            new SelfSerializeSchema());
    }

    class SelfSerializeSchema implements SourceCollect {

        @Override
        public List<CollectResult> collect(Records records, CollectMetaData collectMetaData) {
            List<CollectResult> collectResults = new ArrayList<>();
            if (UpperAccountCreateEvent.class.getName().equals(records.getKey())) {
                UpperAccountCreateEvent event = JsonUtils.decode(new String(records.getValue()),
                    records.getKey());
                List<CollectResult> resultList = event.getAccounts().stream()
                    .map(account -> AccountCreateCmd.newBuilder()
                        .setAccountCode(account)
                        .setBalanceAmt(event.getAmt())
                        .build()
                    ).map(accountCreateCmd -> new CollectResult(accountCreateCmd, true))
                    .collect(Collectors.toList());
                collectResults.addAll(resultList);
            }
            log.info("deserialize return size: " + collectResults.size());
            return collectResults;
        }
    }

}
