package com.iquantex.shoppingcart.coreapi.inventory;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2020/4/2 5:01 PM
 */
@Getter
@Builder
public class InventoryAllocateFailEvent implements Serializable {

    /** 商品ID **/
    private String itemId;

    /** 商品出入库数量(正/负->入库/出库) **/
    private int allocateQty;

}
