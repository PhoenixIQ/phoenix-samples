package com.iquantex.samples.inverter.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alibaba.fastjson.JSON;
import com.iquantex.samples.inverter.domain.entity.Inverter;
import com.iquantex.samples.inverter.dto.InverterDTO;
import com.iquantex.samples.inverter.service.InvertService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InvertController.class)
@EnableWebMvc
@AutoConfigureMockMvc
public class InvertControllerTest {

  @MockBean private InvertService invertService;

  @Autowired private MockMvc mockMvc;

  private static final String DEVICE_CODE = "001";
  private static final String KEY = "shenzhen";

  /** 增加或者改变逆变器实时数据 */
  @Test
  @SneakyThrows
  public void addInvertTest() {
    Inverter inverter = getInverter();

    when(invertService.changeInverterData(any(InverterDTO.class))).thenReturn(DEVICE_CODE);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/invert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(inverter)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(DEVICE_CODE)));
  }

  /** 获取所有逆变器实时运行数据 */
  @Test
  @SneakyThrows
  public void getInverterInfoTest() {
    List<InverterDTO> inverterList = new ArrayList<>();
    when(invertService.getInverterInfo(any())).thenReturn(inverterList);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/invert")
                .param("deviceCodes", "001")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
  }

  /** 聚合根和Ddata数据绑定测试 */
  @org.junit.jupiter.api.Test
  @SneakyThrows
  public void bindDataTest() {
    when(invertService.bindDdata(DEVICE_CODE, KEY)).thenReturn(DEVICE_CODE);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/invert/" + DEVICE_CODE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(DEVICE_CODE)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(DEVICE_CODE)));
  }

  private Inverter getInverter() {
    Inverter inverter = new Inverter();
    inverter.setValueDate(new Date());
    inverter.setName("逆变器");
    inverter.setPi1(12.88);
    inverter.setPi2(12.88);
    inverter.setPi3(12.88);
    inverter.setPi4(12.88);
    inverter.setDeviceCode("0001");
    return inverter;
  }
}
