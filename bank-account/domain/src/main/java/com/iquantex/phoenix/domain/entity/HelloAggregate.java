package com.iquantex.phoenix.domain.entity;

import com.iquantex.phoenix.coreapi.Hello;
import com.iquantex.phoenix.server.aggregate.entity.CommandHandler;
import com.iquantex.phoenix.server.aggregate.entity.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.model.ActReturn;
import com.iquantex.phoenix.server.aggregate.model.RetCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 账户聚合根
 *
 * @author baozi
 * @date 2020/3/10 16:36
 */
@Slf4j
@Data
@EntityAggregateAnnotation(aggregateRootType = "Hello")
public class HelloAggregate implements Serializable {

	private static final long serialVersionUID = -1L;

	/** 状态: 计数器 */
	private long num;

	/**
	 * hello cmd
	 * @param cmd hello 指令
	 * @return 指令返回信息
	 */
	@CommandHandler(aggregateRootId = "helloId")
	public ActReturn act(Hello.HelloCmd cmd) {
		log.info("Hello World Phoenix...");
		return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage("success")
				.event(Hello.HelloEvent.newBuilder().setHelloId(cmd.getHelloId()).build()).build();
	}

	/**
	 * hello event
	 * @param event 指令事件
	 */
	public void on(Hello.HelloEvent event) {
		num++;
		log.info("Phoenix State: {}", num);
	}

}
