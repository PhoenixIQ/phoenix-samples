package com.iquantex.shoppingcart.coreapi.inventory;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2020/4/2 5:02 PM
 */
@Getter
@Builder
public class InventoryItemQueryEvent implements Serializable {

	/** 商品ID **/
	private String itemId;

	/** 商品库存 **/
	private int balanceQty;

}
