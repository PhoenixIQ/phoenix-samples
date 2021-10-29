package com.iquantex.samples.inverter.service;

import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.distributed.data.DistributeDataManager;
import com.iquantex.samples.inverter.constant.InverterConst;
import com.iquantex.samples.inverter.dto.PhotovoltaicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotovolaicService {

  @Autowired
  private DistributeDataManager distributeDataManager;
  @Autowired
  private PhoenixClient phoenixClient;

  public String addPhotovolaic(PhotovoltaicDTO photovoltaicDTO) {
    // 光伏因素影响逆变器离散率
    distributeDataManager
        .getDistributedDataRemote()
        .addDData(
            photovoltaicDTO.getClass(),
            photovoltaicDTO.getKey(),
            photovoltaicDTO,
            InverterConst.MESSAGE_TOPIC);
    return photovoltaicDTO.getKey();
  }

  public String changephotovolaic(PhotovoltaicDTO photovoltaicDTO) {
    distributeDataManager
        .getDistributedDataRemote()
        .addDData(
            photovoltaicDTO.getClass(),
            photovoltaicDTO.getKey(),
            photovoltaicDTO,
            InverterConst.MESSAGE_TOPIC);
    return photovoltaicDTO.getKey();
  }
}
