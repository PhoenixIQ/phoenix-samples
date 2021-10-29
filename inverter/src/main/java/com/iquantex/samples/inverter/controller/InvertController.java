package com.iquantex.samples.inverter.controller;

import com.iquantex.portal.web.api.Response;
import com.iquantex.samples.inverter.dto.InverterDTO;
import com.iquantex.samples.inverter.service.InvertService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** @author 86187 */
@RestController
@RequestMapping("invert")
public class InvertController {

  @Autowired
  private InvertService invertService;

  @ApiOperation("新增逆变器(模拟逆变器数据修改)")
  @PostMapping
  public Response<String> addInvert(@RequestBody InverterDTO inverterDTO) {
    String result = invertService.changeInverterData(inverterDTO);
    return Response.ok(result, "操作成功");
  }

  @ApiOperation("查询逆变器实时运行数据")
  @GetMapping
  public Response<List<InverterDTO>> getInverterInfo(
      @RequestParam("deviceCodes") List<String> deviceCodes) {
    List<InverterDTO> inverterInfo = invertService.getInverterInfo(deviceCodes);
    return Response.ok(inverterInfo, "查询成功");
  }

  @ApiOperation("绑定逆变器和Ddata")
  @GetMapping("/deviceCode")
  public Response<String> bindDdata(
          @RequestParam("deviceCode") String deviceCode, @RequestParam("key") String key) {
    invertService.bindDdata(deviceCode, key);
    return Response.ok(deviceCode, "绑定成功");
  }
}
