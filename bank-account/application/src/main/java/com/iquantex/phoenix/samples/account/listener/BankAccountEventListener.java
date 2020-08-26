package com.iquantex.phoenix.samples.account.listener;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import com.iquantex.phoenix.eventpublish.core.EventDeserializer;
import com.iquantex.phoenix.eventpublish.deserializer.DefaultMessageDeserializer;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateFailEvent;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateOkEvent;
import com.iquantex.phoenix.samples.account.api.protobuf.Account;
import com.iquantex.phoenix.samples.account.model.AccountStore;
import com.iquantex.phoenix.samples.account.repository.AccountStoreRepository;
import com.iquantex.phoenix.server.message.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BankAccountEventListener implements BatchAcknowledgingMessageListener<String, byte[]> {

    @Autowired
    private AccountStoreRepository                   accountStoreRepository;
    // phoenix反序列化工具
    private final EventDeserializer<byte[], Message> deserializer = new DefaultMessageDeserializer();

    @Override
    @KafkaListener(topics = "${phoenix.server.topic.account-event-pub}")
    public void onMessage(List<ConsumerRecord<String, byte[]>> data, Acknowledgment acknowledgment) {
        // 处理事件
        for (ConsumerRecord<String, byte[]> record : data) {
            try {
                Message eventMsg = deserializer.deserialize(record.value());
                if (eventMsg.getPayload() instanceof Account.AccountCreateEvent) {
                    Account.AccountCreateEvent accountCreateEvent = eventMsg.getPayload();
                    AccountStore accountStore = new AccountStore(accountCreateEvent.getAccountCode(),
                        accountCreateEvent.getBalanceAmt(), 0, 0, 0);
                    accountStoreRepository.save(accountStore);
                } else if (eventMsg.getPayload() instanceof AccountAllocateOkEvent) {
                    AccountAllocateOkEvent accountAllocateOkEvent = eventMsg.getPayload();
                    AccountStore accountStore = accountStoreRepository
                        .findById(accountAllocateOkEvent.getAccountCode()).get();
                    if (accountStore != null) {
                        accountStore.setBalanceAmt(accountStore.getBalanceAmt() + accountAllocateOkEvent.getAmt());
                        if (accountAllocateOkEvent.getAmt() < 0) {
                            accountStore.setSuccessTransferOut(accountStore.getSuccessTransferOut() + 1);
                        } else {
                            accountStore.setSuccessTransferIn(accountStore.getSuccessTransferIn() + 1);
                        }
                        accountStoreRepository.save(accountStore);
                    } else {
                        throw new RuntimeException("no found account:" + accountAllocateOkEvent.getAccountCode());
                    }
                } else if (eventMsg.getPayload() instanceof AccountAllocateFailEvent) {
                    AccountAllocateFailEvent accountAllocateFailEvent = eventMsg.getPayload();
                    AccountStore accountStore = accountStoreRepository.findById(
                        accountAllocateFailEvent.getAccountCode()).get();
                    if (accountStore != null) {
                        accountStore.setFailTransferOut(accountStore.getFailTransferOut() + 1);
                        accountStoreRepository.save(accountStore);
                    } else {
                        throw new RuntimeException("no found account:" + accountAllocateFailEvent.getAccountCode());
                    }
                    accountStoreRepository.save(accountStore);
                } else {
                    log.info("not handler msg:<{}>", eventMsg.identify());
                    return;
                }
                log.info("handle message<{}>", eventMsg.getPayload().toString());
            } catch (Exception e) {
                log.error("handle event error", e);

            }
        }

        // 提交消费位点
        acknowledgment.acknowledge();
    }
}
