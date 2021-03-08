package com.iquantex.samples.shopping.coreapi.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 商品聚合根初始化event
 * 
 * @author zhengjie.shen
 * @date 2020/3/23 10:33
 */
@AllArgsConstructor
@Getter
public class GoodsCreateEvent implements Serializable {

    private long qty;

    private long frozenQty;

}
