package com.iquantex.samples.shopping.coreapi.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 商品查询事件
 *
 * @author zhengjie.shen
 * @date 2020/3/23 10:33
 */
@ToString
@AllArgsConstructor
@Getter
public class GoodsQueryEvent implements Serializable {

	private String goodsCode;

	private long qty;

	private long frozenQty;

}
