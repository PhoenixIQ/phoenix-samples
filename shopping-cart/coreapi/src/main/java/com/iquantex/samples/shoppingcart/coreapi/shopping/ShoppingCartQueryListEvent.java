package com.iquantex.samples.shoppingcart.coreapi.shopping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author baozi
 * @date 2020/4/2 6:24 PM
 */
@Getter
@Builder
public class ShoppingCartQueryListEvent implements Serializable {

	/** 用户ID **/
	private String userId;

	/** 商品信息 **/
	private List<ItemRsp> itemRspList;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ItemRsp implements Serializable {

		/** 商品ID **/
		private String itemId;

		/** 购买数量 **/
		private int qty;

		/** 是否可以购买 **/
		private boolean isBuy;

	}

}
