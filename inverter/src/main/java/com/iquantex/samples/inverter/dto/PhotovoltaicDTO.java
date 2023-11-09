package com.iquantex.samples.inverter.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/** @author 86187 光伏 */
@Getter
@Setter
public class PhotovoltaicDTO implements Serializable {
  private static final long serialVersionUID = 42L;
  /**
   * Ddata和聚合根对象绑定的唯一标识
   *
   * <p>实例 key：shenzhen value：0.8 这里的value实际用来替换(改变)inverter中的radio字段值
   */
  private String key;

  private double value;
}
