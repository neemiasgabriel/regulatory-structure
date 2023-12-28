package com.picpay.entrypoint.api.contract;

import com.picpay.entrypoint.api.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public final class HealthControllerImpl implements HealthController {
  @Override
  public ResponseDto template() {
    return new ResponseDto("Teste");
  }
}
