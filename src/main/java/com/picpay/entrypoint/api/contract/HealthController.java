package com.picpay.entrypoint.api.contract;


import com.picpay.entrypoint.api.dto.ResponseDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

public sealed interface HealthController permits HealthControllerImpl {
  @ApiOperation(value = "Api template ")
  @GetMapping(value = "/v1/template")
  ResponseDto template();
}
