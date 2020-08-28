package com.iquantex.phoenix.samples.account.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author baozi
 * @date 2019/12/12 3:44 下午
 * @Description 账户划拨失败事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountAllocateFailEvent implements Serializable {

    private String accountCode; // 划拨账户

    private double amt;        // 划拨金额

    private String result;     // 失败原因

}
