package com.iquantex.samples.inverter.consumer;

import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.core.message.Message;
import com.iquantex.phoenix.distributed.data.DistributeDataManager;
import com.iquantex.phoenix.eventpublish.core.EventDeserializer;
import com.iquantex.phoenix.eventpublish.deserializer.DefaultMessageDeserializer;
import com.iquantex.samples.inverter.domain.api.msg.InverterData;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventConsumer {

  @Autowired private PhoenixClient client;

  @Autowired private DistributeDataManager distributeDataManager;

  private EventDeserializer<byte[], Message> deserializer = new DefaultMessageDeserializer();

  @KafkaListener(
      topics = "${quantex.phoenix.event-publish.event-task.topic}",
      groupId = "phoenix-demo")
  public void receive(List<ConsumerRecord<String, byte[]>> eventBytesList) throws Exception {
    for (ConsumerRecord<String, byte[]> message : eventBytesList) {
      Message eventMsg = deserializer.deserialize(message.value());
      if (eventMsg.getPayload() instanceof InverterData.ChangeEvent) {
        log.info(
            "获取到计算event:{},离散率值为：{}",
            ((InverterData.ChangeEvent) eventMsg.getPayload()).getDeviceCode(),
            ((InverterData.ChangeEvent) eventMsg.getPayload()).getDispersionRatio());
      }
    }
  }
}
