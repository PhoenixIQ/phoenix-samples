package com.iquantex.shoppingcart.domain.shopping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2020/4/2 6:08 PM
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemEntity implements Serializable {

	/** 商品ID **/
	private String itemId;

	/** 商品购买数量 **/
	private int qty;

	/**
	 * 是否可以调整qty: 购物车数量不能小于o
	 * @param optionQty
	 * @return
	 */
	public boolean canOptionQty(int optionQty) {
		return qty + optionQty < 0;
	}

	/**
	 * 调整Qty
	 * @param optionQty
	 */
	public void optionQty(int optionQty) {
		this.qty += optionQty;
	}

}
