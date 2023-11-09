package com.iquantex.samples.helloworld.domain;

import java.io.Serializable;

import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.server.aggregate.ActReturn;
import com.iquantex.phoenix.server.aggregate.CommandHandler;
import com.iquantex.phoenix.server.aggregate.EntityAggregateAnnotation;

import com.iquantex.samples.helloworld.coreapi.HelloCmd;
import com.iquantex.samples.helloworld.coreapi.HelloEvent;
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

	/**
	 * 状态: 计数器
	 */
	private long num;

	/**
	 * 处理hello指令,产生HelloEvent
	 * @param cmd hello 指令
	 * @return 处理结果
	 */
	@CommandHandler(aggregateRootId = "helloId")
	public ActReturn act(HelloCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS)
				.retMessage("Hello World Phoenix " + cmd.getHelloId() + "...").event(new HelloEvent(cmd.getHelloId()))
				.build();
	}

	/**
	 * 处理helloEvent
	 * @param event hello事件
	 */
	public void on(HelloEvent event) {
		num++;
		log.info("Phoenix State: {}", num);
	}

}
