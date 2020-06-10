package com.iquantex.account.subscribe;

import com.google.protobuf.InvalidProtocolBufferException;
import com.iquantex.account.model.AccountStore;
import com.iquantex.account.repository.AccountStoreRepository;
import com.iquantex.phoenix.eventpublish.core.EventDeserializer;
import com.iquantex.phoenix.eventpublish.deserializer.DefaultMessageDeserializer;
import com.iquantex.phoenix.server.aggregate.DomainEvent;
import com.iquantex.phoenix.server.message.Message;
import com.iquantex.samples.account.coreapi.event.AccountAllocateFailEvent;
import com.iquantex.samples.account.coreapi.event.AccountAllocateOkEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class BankAccountEventListener implements BatchAcknowledgingMessageListener<String, byte[]> {

	@Autowired
	private AccountStoreRepository accountStoreRepository;

	/** phoenix序列化工具 */
	private final EventDeserializer<byte[], Message> deserializer = new DefaultMessageDeserializer();

	/** 版本校验集合 */
	private final Map<String/* AggregateId */, Long> lastEventVersion = new HashMap<>();

	@Override
	@KafkaListener(topics = "bank-account-event-pub")
	public void onMessage(List<ConsumerRecord<String, byte[]>> data, Acknowledgment acknowledgment) {
		for (ConsumerRecord<String, byte[]> record : data) {
			eventVersionCheck(record.value());
			tryAddNewAccount(record.value());
		}
		acknowledgment.acknowledge();
	}

	private void tryAddNewAccount(byte[] eventBytes) {
		try {

			Message eventMsg = deserializer.deserialize(eventBytes);
			if (eventMsg.getPayload() instanceof AccountAllocateOkEvent) {
				AccountAllocateOkEvent accountAllocateOkEvent = eventMsg.getPayload();
				AccountStore accountStore = accountStoreRepository.findById(accountAllocateOkEvent.getAccountCode())
						.orElse(new AccountStore(accountAllocateOkEvent.getAccountCode(), 1000, 0, 0, 0));
				accountStore.setBalanceAmt(accountStore.getBalanceAmt() + accountAllocateOkEvent.getAmt());
				if (accountAllocateOkEvent.getAmt() < 0) {
					accountStore.setSuccessTransferOut(accountStore.getSuccessTransferOut() + 1);
				}
				else {
					accountStore.setSuccessTransferIn(accountStore.getSuccessTransferIn() + 1);
				}
				accountStoreRepository.save(accountStore);
				log.info("handle message<{}>", accountAllocateOkEvent);
			}

			if (eventMsg.getPayload() instanceof AccountAllocateFailEvent) {
				AccountAllocateFailEvent accountAllocateFailEvent = eventMsg.getPayload();
				AccountStore accountStore = accountStoreRepository.findById(accountAllocateFailEvent.getAccountCode())
						.orElse(new AccountStore(accountAllocateFailEvent.getAccountCode(), 1000, 0, 0, 0));
				accountStore.setFailTransferOut(accountStore.getFailTransferOut() + 1);
				accountStoreRepository.save(accountStore);
				log.info("handle message<{}>", accountAllocateFailEvent);
			}
		}
		catch (Exception e) {
			log.error("handle event error", e);
		}
	}

	private void eventVersionCheck(byte[] eventBytes) {
		try {
			DomainEvent domainEvent = new DomainEvent(eventBytes);
			String id = domainEvent.getAggregateId();
			long version = domainEvent.getVersion();
			if (!lastEventVersion.containsKey(id)) {
				lastEventVersion.put(id, version);
			}
			else {
				if (version != lastEventVersion.get(id) + 1) {
					log.error("ERROR EVENT VERSION: LAST<{}>, NEW<{}>, ID<{}>", lastEventVersion.get(id), version, id);
				}
				else {
					log.debug("event order check: id<{}>, last<{}>, new<{}>", id, lastEventVersion.get(id), version);
					lastEventVersion.put(id, version);
				}
			}
		}
		catch (InvalidProtocolBufferException e) {
			throw new RuntimeException("deserialize domain event failed");
		}
	}

}
