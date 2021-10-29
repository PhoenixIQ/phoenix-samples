package com.iquantex.samples.inverter.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/** @author 86187 */
@Getter
@Setter
public class InverterDTO implements Serializable {

  private static final long serialVersionUID = 42L;

  private Long id;

  private String name;

  private String deviceCode;

  private double pi1;

  private double pi2;

  private double pi3;

  private double pi4;

  /** 离散率值 */
  private double dispersionRatio;

  private Date valueDate;

  private double radio;
}
