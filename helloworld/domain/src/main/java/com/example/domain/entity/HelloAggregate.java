package com.example.domain.entity;

import com.example.coreapi.Hello;
import com.iquantex.phoenix.server.aggregate.model.ActReturn;
import com.iquantex.phoenix.server.aggregate.entity.AggregateRootIdAnnotation;
import com.iquantex.phoenix.server.aggregate.entity.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.model.RetCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

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
	@AggregateRootIdAnnotation(aggregateRootId = "helloId")
	public ActReturn act(Hello.HelloCmd cmd) {
		log.info("Hello World Phoenix...");
		return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage("success")
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
