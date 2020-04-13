package com.iquantex.samples.shoppingcart.coreapi.shopping;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2020/4/2 6:23 PM
 *
 * 购物车操作类, 简单起见: qty正负代表增加和减少,减少为0时删除该商品
 */
@Getter
@Builder
public class ShoppingCartOptionCmd implements Serializable {

	/** 用户ID **/
	private String userId;

	/** 商品ID **/
	private String itemId;

	/** 商品购买数量 **/
	private int qty;

}
