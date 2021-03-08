package com.iquantex.samples.shopping.domain;

import com.iquantex.phoenix.server.message.ExceptionEvent;
import com.iquantex.phoenix.transaction.aggregate.transaction.SagaAction;
import com.iquantex.phoenix.transaction.aggregate.transaction.TccAction;
import com.iquantex.phoenix.transaction.aggregate.transaction.TransactionAggregateAnnotation;
import com.iquantex.phoenix.transaction.aggregate.transaction.TransactionReturn;
import com.iquantex.phoenix.transaction.aggregate.transaction.TransactionStart;
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

import java.io.Serializable;
import java.util.UUID;

@TransactionAggregateAnnotation(aggregateRootType = "ShoppingAggregateSagaTcc")
public class ShoppingAggregateSagaTcc implements Serializable {

    private static final long serialVersionUID = 7007603076743033374L;
    private BuyGoodsCmd request;
    private String            remark           = "商品购买成功";

    @TransactionStart
    public TransactionReturn on(BuyGoodsCmd request) {
        this.request = request;
        double frozenAmt = request.getQty() * request.getPrice();
        return TransactionReturn
            .builder()
            .addAction(
                TccAction.builder().tryCmd(new AccountTryCmd(request.getAccountCode(), frozenAmt))
                    .confirmCmd(new AccountConfirmCmd(request.getAccountCode(), frozenAmt))
                    .cancelCmd(new AccountCancelCmd(request.getAccountCode(), frozenAmt)).targetTopic("")
                    .subTransId(UUID.randomUUID().toString()).build())
            .addMetaData("request", request)
            .addAction(
                SagaAction.builder().targetTopic("").tiCmd(new GoodsSellCmd(request.getGoodsCode(), request.getQty()))
                    .ciCmd(new GoodsSellCompensateCmd(request.getGoodsCode(), request.getQty()))
                    .subTransId(UUID.randomUUID().toString()).build()).addMetaData("try", "ok").build();
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

    public TransactionReturn on(ExceptionEvent event) {
        remark = remark + event.getErrMsg() + ";";
        return null;
    }

    public Object onFinish() {
        return new BuyGoodsEvent(remark);
    }

}
