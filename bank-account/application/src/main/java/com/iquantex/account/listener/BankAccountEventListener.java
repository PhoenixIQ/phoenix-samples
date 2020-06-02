package com.iquantex.account.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import com.iquantex.account.model.AccountStore;
import com.iquantex.account.repository.AccountStoreRepository;
import com.iquantex.phoenix.eventpublish.core.EventDeserializer;
import com.iquantex.phoenix.eventpublish.deserializer.DefaultMessageDeserializer;
import com.iquantex.phoenix.server.aggregate.DomainEvent;
import com.iquantex.phoenix.server.message.Message;
import com.iquantex.samples.account.coreapi.AccountAllocateOkEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BankAccountEventListener implements BatchAcknowledgingMessageListener<String, byte[]> {

	@Autowired
	private AccountStoreRepository accountStoreRepository;

	private EventDeserializer<byte[], Message> deserializer = new DefaultMessageDeserializer();

	private Map<String/* AggregateId */, Long> lastEventVersion = new HashMap<>();

	private long eventCounter = 0;

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
			eventCounter++;
			if (eventMsg.getPayload() instanceof AccountAllocateOkEvent) {
				String accountCode = ((AccountAllocateOkEvent) eventMsg.getPayload()).getAccountCode();
				accountStoreRepository.save(new AccountStore(accountCode));
				log.info("added new account code<{}> into account store", accountCode);
			}
			log.debug("get event msg from topic<{}>, className<{}>, cnt<{}>", "bank-account-event-pub",
					eventMsg.getPayloadClassName(), eventCounter);
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
