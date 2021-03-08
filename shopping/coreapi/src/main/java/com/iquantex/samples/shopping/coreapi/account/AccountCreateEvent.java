package com.iquantex.samples.shopping.coreapi.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 银行转账初始化event
 * 
 * @author zhengjie.shen
 * @date 2020/3/23 10:28
 */
@AllArgsConstructor
@Getter
public class AccountCreateEvent implements Serializable {

    private double amt;

    private double frozenAmt;

}
