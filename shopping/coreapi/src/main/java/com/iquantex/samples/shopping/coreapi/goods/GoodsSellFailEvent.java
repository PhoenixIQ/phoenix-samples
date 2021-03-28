package com.iquantex.samples.shopping.coreapi.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class GoodsSellFailEvent implements Serializable {

	private String goodsCode;

	private long qty;

	private String remark;

}
