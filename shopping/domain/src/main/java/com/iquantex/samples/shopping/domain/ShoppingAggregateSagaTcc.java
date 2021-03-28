package com.iquantex.samples.shopping.domain;

import java.io.Serializable;
import java.util.UUID;

import com.iquantex.phoenix.core.message.protobuf.Phoenix;
import com.iquantex.phoenix.transaction.aggregate.SagaAction;
import com.iquantex.phoenix.transaction.aggregate.TccAction;
import com.iquantex.phoenix.transaction.aggregate.TransactionAggregateAnnotation;
import com.iquantex.phoenix.transaction.aggregate.TransactionReturn;
import com.iquantex.phoenix.transaction.aggregate.TransactionStart;
import com.iquantex.samples.shopping.coreapi.account.AccountCancelCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountCancelOkEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountConfirmCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountConfirmOkEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountTryCmd;
import com.iquantex.samples.shopping.coreapi.account.AccountTryFailEvent;
import com.iquantex.samples.shopping.coreapi.account.AccountTryOkEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsSellCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsSellCompensateCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsSellFailEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsSellOkEvent;
import com.iquantex.samples.shopping.coreapi.transaction.BuyGoodsCmd;
import com.iquantex.samples.shopping.coreapi.transaction.BuyGoodsEvent;

@TransactionAggregateAnnotation(aggregateRootType = "ShoppingAggregateSagaTcc")
public class ShoppingAggregateSagaTcc implements Serializable {

	private static final long serialVersionUID = 7007603076743033374L;

	private BuyGoodsCmd request;

	private String remark = "商品购买成功";

	@TransactionStart
	public TransactionReturn on(BuyGoodsCmd request) {
		this.request = request;
		double frozenAmt = request.getQty() * request.getPrice();
		return TransactionReturn.builder()
				.addAction(TccAction.builder().tryCmd(new AccountTryCmd(request.getAccountCode(), frozenAmt))
						.confirmCmd(new AccountConfirmCmd(request.getAccountCode(), frozenAmt))
						.cancelCmd(new AccountCancelCmd(request.getAccountCode(), frozenAmt)).targetTopic("")
						.subTransId(UUID.randomUUID().toString()).build())
				.addAction(SagaAction.builder().targetTopic("")
						.tiCmd(new GoodsSellCmd(request.getGoodsCode(), request.getQty()))
						.ciCmd(new GoodsSellCompensateCmd(request.getGoodsCode(), request.getQty()))
						.subTransId(UUID.randomUUID().toString()).build())
				.build();
	}

	public TransactionReturn on(AccountTryOkEvent event) {
		return null;
	}

	public TransactionReturn on(GoodsSellOkEvent event) {
		return null;
	}

	public TransactionReturn on(AccountConfirmOkEvent event) {
		return null;
	}

	public TransactionReturn on(AccountCancelOkEvent event) {
		return null;
	}

	public TransactionReturn on(AccountTryFailEvent event) {
		remark = remark + event.getRemark() + ";";
		return null;
	}

	public TransactionReturn on(GoodsSellFailEvent event) {
		remark = remark + event.getRemark() + ";";
		return null;
	}

	public TransactionReturn on(Phoenix.ExceptionEvent event) {
		remark = remark + event.getErrorStack() + ";";
		return null;
	}

	public Object onFinish() {
		return new BuyGoodsEvent(remark);
	}

}
