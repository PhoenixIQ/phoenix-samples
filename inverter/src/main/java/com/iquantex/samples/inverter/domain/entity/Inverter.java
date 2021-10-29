package com.iquantex.samples.inverter.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iquantex.phoenix.dgc.annotation.Compute;
import com.iquantex.phoenix.dgc.annotation.Model;
import com.iquantex.phoenix.dgc.annotation.Observable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** @author 86187 */
@Getter
@Setter
@Model
@Slf4j
@ApiModel("逆变器")
public class Inverter implements Serializable {

  private static final long serialVersionUID = 42L;

  @Observable
  @ApiModelProperty("设备名称")
  private String name;

  @Observable
  @ApiModelProperty("设备编码")
  private String deviceCode;

  @Observable
  @ApiModelProperty("支路电流1")
  private double pi1;

  @Observable
  @ApiModelProperty("支路电流2")
  private double pi2;

  @Observable
  @ApiModelProperty("支路电流3")
  private double pi3;

  @Observable
  @ApiModelProperty("支路电流4")
  private double pi4;

  @Observable
  @ApiModelProperty("影响因素")
  private double radio;

  /** 离散率值 */
  @ApiModelProperty("离散率")
  private double dispersionRatio;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ApiModelProperty("上报时间")
  private Date valueDate;

  /** 获取电流离散率(简化-平均值) */
  @Compute
  public double getDispersionRatio() {
    log.info("{} 计算离散率.....", name);
    return (getPi1() + getPi2() + getPi3() + getPi4()) / 4 * getRadio();
  }
}
