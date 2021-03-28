package com.iquantex.samples.shopping.coreapi.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class BuyGoodsEvent implements Serializable {

	private String remark;

}
