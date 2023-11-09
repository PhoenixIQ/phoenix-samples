package com.iquantex.phoenix.samples.account.domain;

import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.samples.account.api.command.AccountQueryCmd;
import com.iquantex.phoenix.samples.account.api.event.AccountQueryEvent;
import com.iquantex.phoenix.server.aggregate.ActReturn;
import com.iquantex.phoenix.server.aggregate.QueryHandler;
import com.iquantex.phoenix.server.aggregate.cls.AggregateSegment;

/**
 * 聚合根片段代码
 */
@AggregateSegment
public interface BankAccountQueryBehavior {

	/**
	 * 处理查询命令
	 * @param cmd
	 * @return
	 */
	@QueryHandler(aggregateRootId = "accountCode")
	default ActReturn act(AccountQueryCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS).retMessage("The query is successful").event(genQueryEvent())
				.build();
	}

	/**
	 * 因为查询时间依赖状态, 但接口无法定义成员, 因为通过模版方法获取, 让实现类自定义如何组成返回事件. <strong>这里不能用 getQueryEvent, 不然
	 * Console 的 FASTJSON 读取时会当作这里是一个 field.</strong>
	 * @return
	 */
	AccountQueryEvent genQueryEvent();

}
