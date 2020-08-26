package com.iquantex.phoenix.samples.account.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2019/12/12 3:49 下午
 * @Description 账户划拨成功事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountAllocateOkEvent implements Serializable {

    private String accountCode; // 划拨账户

    private double amt;        // 划拨金额

}
