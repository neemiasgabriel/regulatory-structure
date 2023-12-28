package com.picpay.core.validator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.picpay.core.common.helper.LogGenerator;
import com.picpay.core.enums.CorpEnum;
import org.springframework.stereotype.Component;

import static com.picpay.core.enums.GroupsEnum.ONE;
import static com.picpay.core.enums.GroupsEnum.THREE;
import static com.picpay.core.enums.GroupsEnum.TWO;
import static com.picpay.core.enums.GroupsEnum.ZERO;


@Slf4j
@Component
@RequiredArgsConstructor
public final class PatternValidator {
  private static final int GROUPS_LENGTH = 4;
  private static final String UNDEFINED_FORMAT = " não está no formato definido";

  private final DateValidator dateValidator;
  private final LogGenerator logGenerator;

  /**
   * Padrão do arquivo: Contabil_CORP_ddMMyyyy_yyyyMMddHHmmSS.txt
   *
   * @param fileName Nome do arquivo
   * @return True, se o arquivo estiver no formato desejado. Falso, caso contrário
   */
  public boolean hasFilePattern(final String fileName) {
    final var groups = fileName.split("_");

    if (groups.length != GROUPS_LENGTH) {
      log.info("O arquivo " + fileName + UNDEFINED_FORMAT);
      return false;
    }

    boolean anwser = false;

    final var accounting = groups[ZERO.number()];
    final var corp = groups[ONE.number()];
    final var date = groups[TWO.number()];
    final var timestamp = groups[THREE.number()].replace(".txt", "");

    if (!"contabil".equalsIgnoreCase(accounting)) {
      log.info(logGenerator.logMsg(fileName, "Não é um arquivo contábil"));
    } else if (CorpEnum.getCorpEnum(corp) == null) {
      log.info(logGenerator.logMsg(fileName, " Não é de uma empresa válida"));
    } else if (!dateValidator.isDayMonthYear(date)) {
      log.info(logGenerator.logMsg(fileName, "Data do arquivo" + UNDEFINED_FORMAT));
      log.info("A data do arquivo " + fileName + UNDEFINED_FORMAT);
    } else if (!dateValidator.isTimestamp(timestamp)) {
      log.info(logGenerator.logMsg(fileName, "O timestamp informado do arquivo" + UNDEFINED_FORMAT));
    } else {
      anwser = true;
    }

    return anwser;
  }
}
