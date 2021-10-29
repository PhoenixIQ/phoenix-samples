package com.iquantex.samples.inverter.domain;

import com.iquantex.phoenix.core.message.RetCode;
import com.iquantex.phoenix.server.aggregate.*;
import com.iquantex.phoenix.server.aggregate.inner.cmd.DdataChangeCmd;
import com.iquantex.phoenix.server.aggregate.inner.event.DdataChangeEvent;
import com.iquantex.phoenix.server.eventstore.snapshot.SelectiveSnapshot;
import com.iquantex.samples.inverter.domain.api.msg.InverterData;
import com.iquantex.samples.inverter.domain.entity.Inverter;
import com.iquantex.samples.inverter.domain.manger.DgcInvertManger;
import com.iquantex.samples.inverter.domain.snapshot.InverterSnapshot;
import com.iquantex.samples.inverter.domain.transformer.InverterTransformer;
import com.iquantex.samples.inverter.domain.utils.ReportDistributedDataHelper;
import com.iquantex.samples.inverter.dto.InverterDTO;
import com.iquantex.samples.inverter.dto.PhotovoltaicDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

/** @author 86187 */
@EntityAggregateAnnotation(aggregateRootType = "invert", snapshotInterval = 2)
@Slf4j
@Getter
@Setter
public class InvertAggregate implements Serializable, SelectiveSnapshot<InverterSnapshot> {

  private static final long serialVersionUID = 42L;

  private DgcInvertManger invertManger = new DgcInvertManger();

  @CommandHandler(
      aggregateRootId = {"deviceCode"},
      isCommandSourcing = true)
  public ActReturn act(InverterData.ChangeCmd cmd) {

    // 测试EntityAggregateContext.invoke方法  主要用于服务重启数据恢复时，确保随机数据的唯一性
    Integer i = EntityAggregateContext.invoke("inverter", () -> new Random().nextInt(10));
    log.info("i:{}", i);

    Inverter inverter = invertManger.getInverter();
    if (inverter == null) {
      inverter = EntityAggregateContext.getDgcObjectManager().getDgcObject(Inverter.class);
      InverterTransformer.fillInverter(inverter, cmd);
      invertManger.setInverter(inverter);
    } else {
      updateInverter(cmd, inverter);
    }
    InverterData.ChangeEvent event = new InverterData.ChangeEvent();
    event.setDeviceCode(cmd.getDeviceCode());
    event.setDispersionRatio(inverter.getDispersionRatio());
    return ActReturn.builder().retCode(RetCode.SUCCESS).event(event).build();
  }

  @CommandHandler(
      aggregateRootId = {"deviceCode"},
      isCommandSourcing = true)
  public ActReturn act(InverterData.BindCmd cmd) {
    // 将ddata和聚合根进行绑定
    PhotovoltaicDTO photovoltaicDTO =
        ReportDistributedDataHelper.getData(PhotovoltaicDTO.class, cmd.getKey());
    Inverter inverter = invertManger.getInverter();
    inverter.setRadio(photovoltaicDTO.getValue());
    return ActReturn.builder().retCode(RetCode.SUCCESS).event("event").build();
  }

  /** 查询单个逆变器或者多个逆变器数据 */
  @QueryHandler(aggregateRootId = {"deviceCode"})
  public ActReturn act(InverterData.QueryCmd cmd) {
    log.info("获取到查询cmd");
    Inverter inverter = invertManger.getInverter();
    InverterDTO inverterDTO = InverterTransformer.toInverterDTO(inverter);
    InverterData.QueryEvent event = new InverterData.QueryEvent();
    event.setInverterDTO(inverterDTO);
    return ActReturn.builder().retCode(RetCode.SUCCESS).event(event).build();
  }

  // 用于测试Ddata数据和聚合根的绑定
  @InnerCommandHandler
  public ActReturn act(DdataChangeCmd cmd) {
    log.info("综合事件被触发！！{},重新计算逆变器离散率", cmd.getAggregateId());
    Inverter inverter = invertManger.getInverter();
    PhotovoltaicDTO photovoltaicDTO = (PhotovoltaicDTO) cmd.getDdata();
    inverter.setRadio(photovoltaicDTO.getValue());
    inverter.getDispersionRatio();
    return ActReturn.builder()
        .retCode(RetCode.SUCCESS)
        .retMessage("ok")
        .event(new DdataChangeEvent())
        .build();
  }

  private void updateInverter(InverterData.ChangeCmd cmd, Inverter mangerInverter) {
    mangerInverter.setDeviceCode(cmd.getDeviceCode());
    mangerInverter.setName(cmd.getName());
    mangerInverter.setValueDate(new Date());
    // 判断电流值是否改变
    boolean flage =
        cmd.getPi1() == mangerInverter.getPi1()
            && cmd.getPi2() == mangerInverter.getPi2()
            && cmd.getPi3() == mangerInverter.getPi3()
            && cmd.getPi4() == mangerInverter.getPi4();
    if (!flage) {
      mangerInverter.setPi1(cmd.getPi1());
      mangerInverter.setPi2(cmd.getPi2());
      mangerInverter.setPi3(cmd.getPi3());
      mangerInverter.setPi4(cmd.getPi4());
    }
  }

  @Override
  public void loadSnapshot(InverterSnapshot data) {
    log.info("开始加载快照 逆变器：{}", data.getInverterDTO().getName());
    invertManger.setInverter(InverterTransformer.toInverter(data.getInverterDTO()));
  }

  @Override
  public InverterSnapshot saveSnapshot() {
    log.info("开始生成快照 逆变器：{}", invertManger.getInverter().getName());
    InverterSnapshot inverterSnapshot = new InverterSnapshot();
    Inverter inverter = invertManger.getInverter();
    inverterSnapshot.setInverterDTO(InverterTransformer.toInverterDTO(inverter));
    return inverterSnapshot;
  }
}
