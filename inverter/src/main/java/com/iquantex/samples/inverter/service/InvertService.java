package com.iquantex.samples.inverter.service;

import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.client.RpcResult;
import com.iquantex.samples.inverter.constant.InverterConst;
import com.iquantex.samples.inverter.domain.api.msg.InverterData;
import com.iquantex.samples.inverter.dto.InverterDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/** @author 86187 */
@Service
@Slf4j
public class InvertService {
  /** phoenix 客户端 */
  @Autowired private PhoenixClient client;

  public List<InverterDTO> getInverterInfo(List<String> deviceCodes) {
    if (CollectionUtils.isEmpty(deviceCodes)) {
      throw new RuntimeException("查询设备编码不能为空！");
    }
    List<InverterDTO> inverterList = new ArrayList<>();
    deviceCodes.forEach(
        t -> {
          InverterData.QueryCmd queryCmd = new InverterData.QueryCmd();
          queryCmd.setDeviceCode(t);
          Future<RpcResult> send =
              client.send(queryCmd, InverterConst.MESSAGE_TOPIC, UUID.randomUUID().toString());
          try {
            RpcResult rpcResult = send.get(InverterConst.RPC_TIME_OUT, TimeUnit.SECONDS);
            InverterData.QueryEvent queryEvent = (InverterData.QueryEvent) rpcResult.getData();
            inverterList.add(queryEvent.getInverterDTO());
          } catch (InterruptedException
              | TimeoutException
              | ExecutionException interruptedException) {
            interruptedException.printStackTrace();
          }
        });
    return inverterList;
  }

  public String changeInverterData(InverterDTO inverterDTO) {
    InverterData.ChangeCmd changeCmd = new InverterData.ChangeCmd();
    BeanUtils.copyProperties(inverterDTO, changeCmd);
    Future<RpcResult> send =
        client.send(changeCmd, InverterConst.MESSAGE_TOPIC, UUID.randomUUID().toString());
    try {
      send.get(InverterConst.RPC_TIME_OUT, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      return "rpc error: " + e.getMessage();
    }

    return changeCmd.getDeviceCode();
  }

  public String bindDdata(String deviceCode, String key) {
    if (StringUtils.isEmpty(deviceCode)) {
      throw new RuntimeException("设备编码不能为空");
    }
    InverterData.BindCmd bindCmd = new InverterData.BindCmd();
    bindCmd.setDeviceCode(deviceCode);
    bindCmd.setKey(key);
    client.send(bindCmd, InverterConst.MESSAGE_TOPIC, UUID.randomUUID().toString());
    return deviceCode;
  }
}
