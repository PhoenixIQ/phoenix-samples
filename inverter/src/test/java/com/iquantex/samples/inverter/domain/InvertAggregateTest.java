package com.iquantex.samples.inverter.domain;

import com.iquantex.phoenix.core.util.JsonUtil;
import com.iquantex.phoenix.server.aggregate.inner.cmd.DdataChangeCmd;
import com.iquantex.phoenix.server.test.EntityAggregateFixture;
import com.iquantex.samples.inverter.domain.api.msg.InverterData;
import com.iquantex.samples.inverter.dto.InverterDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class InvertAggregateTest {

  private EntityAggregateFixture entityAggregateFixture;

  private static final String DEVICE_CODE = "001";

  @Before
  public void init() {
    entityAggregateFixture =
        new EntityAggregateFixture(InvertAggregate.class.getPackage().getName());
  }

  /** 测试聚合根绑定Ddata事件触发 */
  @Test
  public void DdataTest() {
    InverterDTO inverterDTO = getInverterDTO();
    DdataChangeCmd cmd = new DdataChangeCmd();
    cmd.setAggregateId("EA@invert@005");
    cmd.setDdataCode("005");
    cmd.setDdataClassName(InverterDTO.class.getName());
    cmd.setDdata(JsonUtil.encode(inverterDTO));
    entityAggregateFixture.when(cmd).expectRetSuccessCode();
  }

  /** 测试逆变器数据修改触发重新计算离散率 */
  @Test
  public void changInverterInfoTest() {
    InverterData.ChangeCmd cmd = new InverterData.ChangeCmd();
    cmd.setDeviceCode(DEVICE_CODE);
    cmd.setPi1(12.96);
    cmd.setPi2(12.96);
    cmd.setPi3(12.96);
    cmd.setPi4(12.96);
    InverterData.ChangeEvent event =
        entityAggregateFixture
            .when(cmd)
            .expectMessage(InverterData.ChangeEvent.class)
            .expectRetSuccessCode()
            .getLastOutMsg()
            .getPayload();
    Assert.assertEquals(DEVICE_CODE, event.getDeviceCode());
    Assert.assertEquals(12.96, event.getDispersionRatio(), 0.0);
  }

  /** 测试逆变器数据查询 */
  @Test
  public void queryInverterInfoTest() {
    InverterData.ChangeCmd cmd = new InverterData.ChangeCmd();
    cmd.setDeviceCode(DEVICE_CODE);
    cmd.setPi1(12.96);
    cmd.setPi2(12.96);
    cmd.setPi3(12.96);
    cmd.setPi4(12.96);
    entityAggregateFixture
        .when(cmd)
        .expectMessage(InverterData.ChangeEvent.class)
        .expectRetSuccessCode();
    InverterData.QueryCmd queryCmd = new InverterData.QueryCmd();
    queryCmd.setDeviceCode(DEVICE_CODE);
    entityAggregateFixture.when(queryCmd).expectRetSuccessCode();
  }

  private InverterDTO getInverterDTO() {
    InverterDTO inverterDTO = new InverterDTO();
    inverterDTO.setValueDate(new Date());
    inverterDTO.setName("逆变器");
    inverterDTO.setPi1(12.88);
    inverterDTO.setPi2(12.88);
    inverterDTO.setPi3(12.88);
    inverterDTO.setPi4(12.88);
    inverterDTO.setDeviceCode("0001");
    return inverterDTO;
  }
}
