package com.iquantex.samples.shopping.coreapi.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class AccountCancelCmd implements Serializable {

	private String accountCode;

	private double frozenAmt;

}
