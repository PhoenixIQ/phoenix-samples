package com.iquantex.samples.inverter.controller;

import com.iquantex.samples.inverter.dto.PhotovoltaicDTO;
import com.iquantex.samples.inverter.service.PhotovolaicService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** @author 86187 */
@RestController
@RequestMapping("photovoltaic")
public class PhotovoltaicController {

  @Autowired private PhotovolaicService photovolaicService;

  @ApiOperation("新增光伏因素")
  @PostMapping
  public ResponseEntity<String> addPhotovoltaic(@RequestBody PhotovoltaicDTO photovoltaicDTO) {
    String result = photovolaicService.addPhotovolaic(photovoltaicDTO);
    return ResponseEntity.ok(result);
  }

  @ApiOperation("修改光伏因素")
  @PutMapping
  public ResponseEntity<String> updatePhotovoltaic(@RequestBody PhotovoltaicDTO photovoltaicDTO) {
    String result = photovolaicService.changephotovolaic(photovoltaicDTO);
    return ResponseEntity.ok(result);
  }
}
