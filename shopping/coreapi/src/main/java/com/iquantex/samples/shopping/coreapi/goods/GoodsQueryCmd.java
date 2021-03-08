package com.iquantex.samples.shopping.coreapi.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 商品查询命令
 * 
 * @author zhengjie.shen
 * @date 2020/3/23 10:33
 */
@AllArgsConstructor
@Getter
public class GoodsQueryCmd implements Serializable {

    private String goodsCode;
}
