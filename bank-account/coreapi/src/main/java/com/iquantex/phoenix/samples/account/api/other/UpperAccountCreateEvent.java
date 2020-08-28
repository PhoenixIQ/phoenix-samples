package com.iquantex.phoenix.samples.account.api.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author baozi
 * @date 2020/5/27 10:38 AM 模拟上游系统的批量处理的事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpperAccountCreateEvent {

    /**
     * 账户集合
     */
    private List<String> accounts;

    /**
     * 初始化金额
     */
    private double       amt;

}
