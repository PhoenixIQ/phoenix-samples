package com.iquantex.samples.shopping.coreapi.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Getter
public class BuyGoodsCmd implements Serializable {

	private static final long serialVersionUID = -8667685124103764667L;

	private String accountCode;

	private String goodsCode;

	private long qty;

	private double price;

}
