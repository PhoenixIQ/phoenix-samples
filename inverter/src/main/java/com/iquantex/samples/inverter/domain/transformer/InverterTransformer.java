package com.iquantex.samples.inverter.domain.transformer;

import com.iquantex.samples.inverter.constant.InverterConst;
import com.iquantex.samples.inverter.domain.api.msg.InverterData;
import com.iquantex.samples.inverter.domain.entity.Inverter;
import com.iquantex.samples.inverter.dto.InverterDTO;
import java.util.Date;

/** @author 86187 */
public class InverterTransformer {

  public static void fillInverter(Inverter inverter, InverterData.ChangeCmd cmd) {

    inverter.setName(cmd.getName());
    inverter.setDeviceCode(cmd.getDeviceCode());
    inverter.setPi1(cmd.getPi1());
    inverter.setPi2(cmd.getPi2());
    inverter.setPi3(cmd.getPi3());
    inverter.setPi4(cmd.getPi4());
    inverter.setRadio(InverterConst.RADIO);
    inverter.setValueDate(new Date());
  }

  public static InverterDTO toInverterDTO(Inverter inverter) {
    InverterDTO inverterDTO = new InverterDTO();
    inverterDTO.setName(inverter.getName());
    inverterDTO.setDeviceCode(inverter.getDeviceCode());
    inverterDTO.setPi1(inverter.getPi1());
    inverterDTO.setPi2(inverter.getPi2());
    inverterDTO.setPi3(inverter.getPi3());
    inverterDTO.setPi4(inverter.getPi4());
    inverterDTO.setDispersionRatio(inverter.getDispersionRatio());
    inverterDTO.setValueDate(inverter.getValueDate());
    inverterDTO.setRadio(inverter.getRadio());
    return inverterDTO;
  }

  public static Inverter toInverter(InverterDTO inverterDTO) {
    Inverter inverter = new Inverter();
    inverter.setName(inverterDTO.getName());
    inverter.setDeviceCode(inverterDTO.getDeviceCode());
    inverter.setPi1(inverterDTO.getPi1());
    inverter.setPi2(inverterDTO.getPi2());
    inverter.setPi3(inverterDTO.getPi3());
    inverter.setPi4(inverterDTO.getPi4());
    inverter.setRadio(inverterDTO.getRadio());
    inverter.setDispersionRatio(inverterDTO.getDispersionRatio());
    inverter.setValueDate(inverterDTO.getValueDate());
    return inverter;
  }
}
