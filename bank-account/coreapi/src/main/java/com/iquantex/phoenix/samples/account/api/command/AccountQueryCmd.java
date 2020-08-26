package com.iquantex.phoenix.samples.account.api.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2020/3/26 8:10 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountQueryCmd implements Serializable {

    /** 账户编码 */
    private String accountCode;

}
