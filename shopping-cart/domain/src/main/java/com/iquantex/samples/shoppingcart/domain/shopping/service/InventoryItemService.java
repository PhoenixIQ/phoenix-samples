package com.iquantex.samples.shoppingcart.domain.shopping.service;

import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.client.RpcResult;
import com.iquantex.samples.shoppingcart.coreapi.inventory.InventoryItemQueryCmd;
import com.iquantex.samples.shoppingcart.coreapi.inventory.InventoryItemQueryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author baozi 领域服务
 * @date 2020/4/7 8:58 PM
 */
@Service
public class InventoryItemService {

    @Autowired
    private PhoenixClient phoenixClient;

    /**
     * 判断商品库存是否充足
     *
     * @param qty
     * @return
     */
    public boolean inventoryAdequacy(String itemId, int qty) {
        return getInventoryItemBalanceQtyByItemId(itemId) - qty >= 0;
    }

    /**
     * 获取商品库存
     *
     * @param itemId
     * @return
     */
    private int getInventoryItemBalanceQtyByItemId(String itemId) {
        Future<RpcResult> rsp = phoenixClient.send(InventoryItemQueryCmd.builder().itemId(itemId).build(), "shopping-cart", null);
        try {
            RpcResult rpcResult = rsp.get(10, TimeUnit.SECONDS);
            InventoryItemQueryEvent inventoryItem = (InventoryItemQueryEvent) rpcResult.getData();
            return inventoryItem.getBalanceQty();
        } catch (Exception e) {
            // TODO 暂定获取异常为库存为0, 如何支持聚合根降级?
            return 0;
        }
    }

}
