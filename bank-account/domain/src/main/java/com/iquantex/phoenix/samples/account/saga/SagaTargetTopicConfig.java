package com.iquantex.phoenix.samples.account.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class SagaTargetTopicConfig {

    public static volatile String ALLOCATE_CMD_TOPIC = "";

    @Value("${saga-allocate-cmd-topic}")
    private String                allocateCmdTopic;

    @PostConstruct
    private void initConfig() {
        ALLOCATE_CMD_TOPIC = allocateCmdTopic;
        log.info("set allocateCmdTopic=<{}>", allocateCmdTopic);
    }

}
