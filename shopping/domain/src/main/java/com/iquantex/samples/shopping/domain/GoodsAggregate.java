package com.iquantex.samples.shopping.domain;

import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.server.aggregate.ActReturn;
import com.iquantex.phoenix.server.aggregate.CommandHandler;
import com.iquantex.phoenix.server.aggregate.EntityAggregateAnnotation;
import com.iquantex.phoenix.server.aggregate.QueryHandler;
import com.iquantex.samples.shopping.coreapi.goods.GoodsCancelCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsCancelOkEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsConfirmCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsConfirmOkEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsQueryCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsQueryEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsSellCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsSellCompensateCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsSellCompensateOkEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsSellFailEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsSellOkEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsTryCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsTryFailEvent;
import com.iquantex.samples.shopping.coreapi.goods.GoodsTryOkEvent;

import java.io.Serializable;

@EntityAggregateAnnotation(aggregateRootType = "GoodsTcc")
public class GoodsAggregate implements Serializable {

	private static final long serialVersionUID = -6111851668488622895L;

	private long qty;

	private long frozenQty;

	/**
	 * 假定默认100个
	 */
	public GoodsAggregate() {
		this.qty = 100;
		this.frozenQty = 0;
	}

	@QueryHandler(aggregateRootId = "goodsCode")
	public ActReturn act(GoodsQueryCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS)
				.event(new GoodsQueryEvent(cmd.getGoodsCode(), qty, frozenQty)).build();
	}

	@CommandHandler(aggregateRootId = "goodsCode")
	public ActReturn act(GoodsTryCmd cmd) {
		if (qty - frozenQty > cmd.getFrozenQty()) {
			return ActReturn.builder().retCode(RetCode.SUCCESS)
					.event(new GoodsTryOkEvent(cmd.getGoodsCode(), cmd.getFrozenQty())).build();
		}
		else {
			String retMessage = String.format("商品可用不足，剩余:%d, 当前需要冻结:%d", qty - frozenQty, cmd.getFrozenQty());
			return ActReturn.builder().retCode(RetCode.FAIL).retMessage(retMessage)
					.event(new GoodsTryFailEvent(cmd.getGoodsCode(), cmd.getFrozenQty(), retMessage)).build();
		}
	}

	public void on(GoodsTryOkEvent event) {
		frozenQty += event.getFrozenQty();
	}

	public void on(GoodsTryFailEvent event) {
	}

	@CommandHandler(aggregateRootId = "goodsCode")
	public ActReturn act(GoodsConfirmCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS)
				.event(new GoodsConfirmOkEvent(cmd.getGoodsCode(), cmd.getFrozenQty())).build();
	}

	public void on(GoodsConfirmOkEvent event) {
		qty -= event.getFrozenQty();
		frozenQty -= event.getFrozenQty();

	}

	@CommandHandler(aggregateRootId = "goodsCode")
	public ActReturn act(GoodsCancelCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS)
				.event(new GoodsCancelOkEvent(cmd.getGoodsCode(), cmd.getFrozenQty())).build();
	}

	public void on(GoodsCancelOkEvent event) {
		frozenQty -= event.getFrozenQty();

	}

	@CommandHandler(aggregateRootId = "goodsCode")
	public ActReturn act(GoodsSellCmd cmd) {
		if (cmd.getQty() < 0) {
			throw new RuntimeException("数不能小于0");
		}
		if (qty > cmd.getQty()) {
			return ActReturn.builder().retCode(RetCode.SUCCESS)
					.event(new GoodsSellOkEvent(cmd.getGoodsCode(), cmd.getQty())).build();
		}
		else {
			String ret = String.format("余额不足，剩余:%d, 当前需要:%d", qty, cmd.getQty());
			return ActReturn.builder().retCode(RetCode.FAIL).retMessage(ret)
					.event(new GoodsSellFailEvent(cmd.getGoodsCode(), cmd.getQty(), ret)).build();
		}
	}

	public void on(GoodsSellOkEvent event) {
		qty -= event.getQty();
	}

	public void on(GoodsSellFailEvent event) {
	}

	@CommandHandler(aggregateRootId = "goodsCode")
	public ActReturn act(GoodsSellCompensateCmd cmd) {
		return ActReturn.builder().retCode(RetCode.SUCCESS)
				.event(new GoodsSellCompensateOkEvent(cmd.getGoodsCode(), cmd.getQty())).build();
	}

	public void on(GoodsSellCompensateOkEvent event) {
		qty += event.getQty();
	}

}
