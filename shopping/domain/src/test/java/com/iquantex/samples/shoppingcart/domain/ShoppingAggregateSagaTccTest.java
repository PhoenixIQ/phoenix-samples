package com.iquantex.samples.shoppingcart.domain;

import com.iquantex.phoenix.transaction.test.AggregateFixture;
import com.iquantex.samples.shopping.coreapi.account.AccountCreateCmd;
import com.iquantex.samples.shopping.coreapi.goods.GoodsCreateCmd;
import com.iquantex.samples.shopping.coreapi.transaction.BuyGoodsCmd;
import com.iquantex.samples.shopping.domain.AccountAggregate;
import com.iquantex.samples.shopping.domain.ShoppingAggregateSagaTcc;
import org.junit.Test;

/**
 * {@link ShoppingAggregateSagaTcc}
 *
 * @author baozi
 * @author lan
 */
public class ShoppingAggregateSagaTccTest {

    private AggregateFixture getFixture() {
        AggregateFixture fixture = new AggregateFixture(AccountAggregate.class.getPackage().getName());
        AccountCreateCmd accountCreateCmd = new AccountCreateCmd("A1", 100, 0);
        GoodsCreateCmd goodsCreateCmd = new GoodsCreateCmd("book", 10, 0);
        fixture.when(accountCreateCmd).expectRetSuccessCode();
        fixture.when(goodsCreateCmd).expectRetSuccessCode();
        return fixture;
    }

    /**
     * SAGA and TCC: 账户、商品都ok
     */
    @Test
    public void expectRetSuccessCode() {
        AggregateFixture fixture = getFixture();
        BuyGoodsCmd buyGoodsCmd = new BuyGoodsCmd("A1", "book", 1, 10.0);
        fixture.when(buyGoodsCmd).printIdentify().expectRetSuccessCode();
    }

    /**
     * SAGA and TCC: 账户不足
     */
    @Test
    public void expectRetFailCode_account() {
        AggregateFixture fixture = getFixture();
        BuyGoodsCmd buyGoodsCmd = new BuyGoodsCmd("A1", "book", 1, 101.0);
        fixture.when(buyGoodsCmd).printIdentify().expectRetFailCode();
    }

    /**
     * SAGA and TCC: 商品不足
     */
    @Test
    public void expectRetFailCode_goods() {
        AggregateFixture fixture = getFixture();
        BuyGoodsCmd buyGoodsCmd = new BuyGoodsCmd("A1", "book", 11, 1.0);
        fixture.when(buyGoodsCmd).printIdentify().expectRetFailCode();
    }

    /**
     * SAGA and TCC: 商品，账户不足
     */
    @Test
    public void expectRetFailCode_account_goods() {
        AggregateFixture fixture = getFixture();
        BuyGoodsCmd buyGoodsCmd = new BuyGoodsCmd("A1", "book", 11, 10.0);
        fixture.when(buyGoodsCmd).printIdentify().expectRetFailCode();
    }

}
