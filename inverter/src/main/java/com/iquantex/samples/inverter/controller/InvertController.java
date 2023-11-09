package com.iquantex.samples.inverter.controller;

import com.iquantex.samples.inverter.dto.InverterDTO;
import com.iquantex.samples.inverter.service.InvertService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** @author 86187 */
@RestController
@RequestMapping("invert")
public class InvertController {

  @Autowired private InvertService invertService;

  @ApiOperation("新增逆变器(模拟逆变器数据修改)")
  @PostMapping
  public ResponseEntity<String> addInvert(@RequestBody InverterDTO inverterDTO) {
    String result = invertService.changeInverterData(inverterDTO);
    return ResponseEntity.ok(result);
  }

  @ApiOperation("查询逆变器实时运行数据")
  @GetMapping
  public ResponseEntity<List<InverterDTO>> getInverterInfo(
      @RequestParam("deviceCodes") List<String> deviceCodes) {
    List<InverterDTO> inverterInfo = invertService.getInverterInfo(deviceCodes);
    return ResponseEntity.ok(inverterInfo);
  }

  @ApiOperation("绑定逆变器和Ddata")
  @GetMapping("/deviceCode")
  public ResponseEntity<String> bindDdata(
      @RequestParam("deviceCode") String deviceCode, @RequestParam("key") String key) {
    invertService.bindDdata(deviceCode, key);
    return ResponseEntity.ok(deviceCode);
  }
}
