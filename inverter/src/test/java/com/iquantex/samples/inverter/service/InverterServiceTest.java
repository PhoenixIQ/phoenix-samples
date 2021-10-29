package com.iquantex.samples.inverter.service;

import com.iquantex.phoenix.client.PhoenixClient;
import com.iquantex.phoenix.client.RpcResult;
import com.iquantex.phoenix.client.RpcResultCode;
import com.iquantex.samples.inverter.constant.InverterConst;
import com.iquantex.samples.inverter.domain.api.msg.InverterData;
import com.iquantex.samples.inverter.dto.InverterDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InvertService.class)
public class InverterServiceTest {
  @Autowired
  private InvertService invertService;

  @MockBean
  private PhoenixClient phoenixClient;

  private static final String DEVICE_CODE = "001";
  private static final String KEY = "shenzhen";

  @Test
  public void changeInverterInfoTest() {
    InverterDTO inverter = getInverterDTO();
    when(phoenixClient.send(
            any(InverterData.ChangeCmd.class), eq(InverterConst.MESSAGE_TOPIC), anyString()))
        .thenReturn(
            new Future<RpcResult>() {
              @Override
              public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
              }

              @Override
              public boolean isCancelled() {
                return false;
              }

              @Override
              public boolean isDone() {
                return false;
              }

              @Override
              public RpcResult get() throws InterruptedException, ExecutionException {
                return null;
              }

              @Override
              public RpcResult get(long timeout, TimeUnit unit)
                  throws InterruptedException, ExecutionException, TimeoutException {
                return new RpcResult(RpcResultCode.SUCCESS, "", new InverterData.ChangeEvent());
              }
            });
    String inverterData = invertService.changeInverterData(inverter);
    Assert.assertEquals(DEVICE_CODE, inverterData);
    verify(phoenixClient, times(1)).send(any(), any(), any());
  }

  @Test
  public void getInverterInfoTest() {
    List<String> deviceCodeList = new ArrayList<>();
    when(phoenixClient.send(
            any(InverterData.QueryCmd.class), eq(InverterConst.MESSAGE_TOPIC), anyString()))
        .thenReturn(
            new Future<RpcResult>() {
              @Override
              public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
              }

              @Override
              public boolean isCancelled() {
                return false;
              }

              @Override
              public boolean isDone() {
                return false;
              }

              @Override
              public RpcResult get() throws InterruptedException, ExecutionException {
                return null;
              }

              @Override
              public RpcResult get(long timeout, TimeUnit unit)
                  throws InterruptedException, ExecutionException, TimeoutException {
                return new RpcResult(RpcResultCode.SUCCESS, "", new InverterData.QueryEvent());
              }
            });
    deviceCodeList.add("001");
    List<InverterDTO> inverterInfo = invertService.getInverterInfo(deviceCodeList);
    verify(phoenixClient, times(1)).send(any(), any(), any());
  }

  @Test
  public void bindDataTest() {
    when(phoenixClient.send(
            any(InverterData.BindCmd.class), eq(InverterConst.MESSAGE_TOPIC), anyString()))
        .thenReturn(
            new Future<RpcResult>() {
              @Override
              public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
              }

              @Override
              public boolean isCancelled() {
                return false;
              }

              @Override
              public boolean isDone() {
                return false;
              }

              @Override
              public RpcResult get() throws InterruptedException, ExecutionException {
                return null;
              }

              @Override
              public RpcResult get(long timeout, TimeUnit unit)
                  throws InterruptedException, ExecutionException, TimeoutException {
                return null;
              }
            });
    String deviceCode = invertService.bindDdata(DEVICE_CODE, KEY);
    Assert.assertEquals(DEVICE_CODE, deviceCode);
    verify(phoenixClient, times(1)).send(any(), any(), any());
  }

  private InverterDTO getInverterDTO() {
    InverterDTO inverterDTO = new InverterDTO();
    inverterDTO.setValueDate(new Date());
    inverterDTO.setName("逆变器");
    inverterDTO.setPi1(12.88);
    inverterDTO.setPi2(12.88);
    inverterDTO.setPi3(12.88);
    inverterDTO.setPi4(12.88);
    inverterDTO.setDeviceCode("001");
    return inverterDTO;
  }
}
