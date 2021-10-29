package com.iquantex.samples.inverter.domain.snapshot;

import com.iquantex.samples.inverter.dto.InverterDTO;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/** @author 86187 快照数据 */
@Getter
@Setter
public class InverterSnapshot implements Serializable {

  private static final long serialVersionUID = 42L;
  /** 存储逆变器实时数据 */
  private InverterDTO inverterDTO;

  // 存储逆变器历史数据--逆变器24小时的整点数据
  // private Map<String, InverterDTO> inverterDataHisMap = new HashMap<>();
}
