package com.iquantex.phoenix.samples.account.domain;

import java.util.ArrayList;
import java.util.List;

import com.iquantex.phoenix.samples.account.api.protobuf.Account;
import com.iquantex.phoenix.samples.account.api.other.UpperAccountCreateEvent;
import com.iquantex.phoenix.samples.account.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.iquantex.phoenix.server.message.DeserializationReturn;
import com.iquantex.phoenix.server.mq.kafka.AutoOffsetReset;
import com.iquantex.phoenix.server.mq.subscribe.Subscribe;
import com.iquantex.phoenix.server.mq.subscribe.SubscribeMqInfo;

@Slf4j
@Component("WebEventTopicSubscribe")
public class CreateAccountEventSubscribe implements Subscribe {

    @Value("${create-account-event.mqAddress}")
    private String mqAddress;

    @Value("${create-account-event.topic}")
    private String topic;

    @Override
    public SubscribeMqInfo getSubscribeMqInfo() {
        return new SubscribeMqInfo(mqAddress, topic, AutoOffsetReset.earliest);
    }

    @Override
    public List<DeserializationReturn> deserialize(String className, byte[] bytes) {
        List<DeserializationReturn> deserializationReturns = new ArrayList<>();
        // 外部批量聚合根事件转换为多个聚合根的创建cmd
        if (UpperAccountCreateEvent.class.getName().equals(className)) {
            UpperAccountCreateEvent batchAccountCreateEvent = JsonUtils.decode(new String(bytes), className);
            batchAccountCreateEvent.getAccounts().forEach(account -> {
                Account.AccountCreateCmd accountCreateCmd = Account.AccountCreateCmd.newBuilder().setAccountCode(account).setBalanceAmt(batchAccountCreateEvent.getAmt()).build();
                deserializationReturns.add(new DeserializationReturn(accountCreateCmd,  true));
            });
        }
        log.info("deserialize return size: " + deserializationReturns.size());
        return deserializationReturns;
    }
}
