package com.iquantex.phoenix.samples.account.api.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2019/12/12 3:52 下午
 * @Description 账户转账请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferCmd implements Serializable {

    private static final long serialVersionUID = -8951705463515914994L;

    private String            inAccountCode;                           // 转入账户

    private String            outAccountCode;                          // 转出账户

    private double            amt;                                     // 转入金额(正)

}
