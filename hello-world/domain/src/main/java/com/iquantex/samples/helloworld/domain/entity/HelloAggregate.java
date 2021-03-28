package com.iquantex.samples.helloworld.domain.entity;

import java.io.Serializable;

import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.server.aggregate.ActReturn;
import com.iquantex.phoenix.server.aggregate.CommandHandler;
import com.iquantex.phoenix.server.aggregate.EntityAggregateAnnotation;
import com.iquantex.samples.helloworld.coreapi.Hello;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * hello 聚合根
 */
@Slf4j
@Data
@EntityAggregateAnnotation(aggregateRootType = "Hello")
public class HelloAggregate implements Serializable {

	private static final long serialVersionUID = -1L;

	/** 状态: 计数器 */
	private long num;

	/**
	 * 处理hello指令，产生HelloEvent
	 * @param cmd hello 指令
	 * @return 处理结果
	 */
	@CommandHandler(aggregateRootId = "helloId")
	public ActReturn act(Hello.HelloCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage("Hello World Phoenix...")
				.event(Hello.HelloEvent.newBuilder().setHelloId(cmd.getHelloId()).build()).build();
	}

	/**
	 * 处理helloEvent
	 * @param event hello事件
	 */
	public void on(Hello.HelloEvent event) {
		num++;
		log.info("Phoenix State: {}", num);
	}

}
