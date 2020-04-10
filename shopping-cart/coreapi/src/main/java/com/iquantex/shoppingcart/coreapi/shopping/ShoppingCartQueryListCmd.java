package com.iquantex.shoppingcart.coreapi.shopping;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2020/4/2 6:23 PM
 */
@Getter
@Builder
public class ShoppingCartQueryListCmd implements Serializable {

	/** 用户ID **/
	private String userId;

}
