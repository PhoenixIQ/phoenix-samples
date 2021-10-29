package com.iquantex.samples.inverter.domain.utils;

import com.iquantex.phoenix.distributed.data.DistributeDataManager;
import com.iquantex.phoenix.server.aggregate.EntityAggregateContext;
import com.iquantex.samples.inverter.constant.InverterConst;

/**
 * @author 86187
 * 用来获取DDdate  绑定聚合根
 */
public class ReportDistributedDataHelper {

  public static <T> T getData(Class<T> cls, String key) {
    DistributeDataManager distributeDataManager =
        EntityAggregateContext.getAggregateBean(DistributeDataManager.NAME);
    return distributeDataManager
        .getDistributedDataRemote()
        .getDData(cls, key, InverterConst.MESSAGE_TOPIC, InverterConst.MESSAGE_TOPIC);
  }
}
