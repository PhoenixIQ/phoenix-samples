package com.iquantex.samples.inverter.domain.api.msg;

import com.iquantex.samples.inverter.dto.InverterDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/** @author 86187 */
@Getter
@Setter
public class InverterData implements Serializable {
  private static final long serialVersionUID = 42L;

  @Getter
  @Setter
  public static class BindCmd implements Serializable {
    private static final long serialVersionUID = 42L;
    private String deviceCode;
    // 与DData绑定的key
    private String key;
  }

  @Getter
  @Setter
  public static class ChangeCmd implements Serializable {
    private static final long serialVersionUID = 42L;
    private String deviceCode;
    private String name;
    private double pi1;

    private double pi2;

    private double pi3;

    private double pi4;

    private double radio;
  }

  @Getter
  @Setter
  public static class QueryCmd implements Serializable {
    private static final long serialVersionUID = 42L;
    private String deviceCode;
  }

  @Getter
  @Setter
  public static class ChangeEvent implements Serializable {
    private static final long serialVersionUID = 42L;
    private String deviceCode;
    /** 离散率值 */
    private double dispersionRatio;
  }

  @Getter
  @Setter
  public static class QueryEvent implements Serializable {
    private static final long serialVersionUID = 42L;
    private InverterDTO inverterDTO;
  }
}
