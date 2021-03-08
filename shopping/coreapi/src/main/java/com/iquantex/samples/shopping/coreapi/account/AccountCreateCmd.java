package com.iquantex.samples.shopping.coreapi.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 银行转账初始化cmd
 * 
 * @author zhengjie.shen
 * @date 2020/3/23 10:27
 */
@AllArgsConstructor
@Getter
public class AccountCreateCmd implements Serializable {

    private String accountCode;

    private double amt;

    private double frozenAmt;

}
