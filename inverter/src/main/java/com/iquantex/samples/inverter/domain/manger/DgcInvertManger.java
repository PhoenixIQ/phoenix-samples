package com.iquantex.samples.inverter.domain.manger;

import com.iquantex.samples.inverter.domain.entity.Inverter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DgcInvertManger implements Serializable {
  private static final long serialVersionUID = 42L;

  /** 存储逆变器实时数据 */
  private Inverter inverter;

  /** 存储逆变器历史数据--逆变器24小时的整点数据 */
  private Map<String, Inverter> inverterDataHisMap = new HashMap<>();
}
