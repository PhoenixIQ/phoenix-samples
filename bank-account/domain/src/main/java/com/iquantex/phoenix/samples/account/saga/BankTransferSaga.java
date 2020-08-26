package com.iquantex.phoenix.samples.account.saga;

import java.io.Serializable;

import com.iquantex.phoenix.samples.account.api.command.AccountAllocateCmd;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateFailEvent;
import com.iquantex.phoenix.samples.account.api.event.AccountAllocateOkEvent;
import com.iquantex.phoenix.samples.account.api.command.AccountTransferCmd;
import com.iquantex.phoenix.transaction.aggregate.transaction.*;

/**
 * Created by lan on 2019/10/10
 */
@TransactionAggregateAnnotation(aggregateRootType = "BankTransferSaga")
public class BankTransferSaga implements Serializable {

    private static final long  serialVersionUID = 2834866334398657786L;

    // 事务返回内容
    private String             retMessage       = "";

    // 事务请求体
    private AccountTransferCmd request;

    /**
     * 处理转账请求,先发起转出
     * @param request
     * @return
     */
    @TransactionStart
    public TransactionReturn on(AccountTransferCmd request) {
        this.request = request;
        return TransactionReturn
            .builder()
            .addAction(
            // saga事务
                SagaAction.builder().targetTopic(SagaTargetTopicConfig.ALLOCATE_CMD_TOPIC)
                    .tiCmd(new AccountAllocateCmd(request.getOutAccountCode(), -request.getAmt())).build()).build();
    }

    /**
     * 账户划拨正常
     * @param event
     * @return
     */
    public TransactionReturn on(AccountAllocateOkEvent event) {
        // 转出正常,请求转入
        if (event.getAmt() < 0) {
            return TransactionReturn
                .builder()
                .addAction(
                    SagaAction.builder().targetTopic(SagaTargetTopicConfig.ALLOCATE_CMD_TOPIC)
                        .tiCmd(new AccountAllocateCmd(request.getInAccountCode(), request.getAmt())).build()).build();
        }
        // 转入正常,转账结束
        else {
            retMessage = String.format("账户转账成功：转出账户：%s，转入账户：%s，转账金额：%s", request.getOutAccountCode(),
                request.getInAccountCode(), request.getAmt());
            return null;
        }
    }

    /**
     * 账户划拨失败
     * @param event
     * @return
     */
    public TransactionReturn on(AccountAllocateFailEvent event) {
        retMessage = String.format("账户转账失败：转出账户：%s，转入账户：%s，转账金额：%s，失败原因：%s", request.getOutAccountCode(),
            request.getInAccountCode(), request.getAmt(), event.getResult());
        return null;
    }

    /**
     * 事务结束调用方法
     * @return
     */
    public TransactionFinishReturn onFinish() {
        return TransactionFinishReturn.builder().retMessage(retMessage).data("account transfer finish").build();
    }

}
