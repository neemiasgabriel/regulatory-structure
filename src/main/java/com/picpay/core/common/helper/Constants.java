package com.picpay.core.common.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
  public static final String S3_ROOT_FOLDER = "picpaybank/regulatory/accounting_gateway";
  public static final String S3_IN = "/in";
  public static final String S3_OUT = "/out";
  public static final String S3_PROCESSED = "/processed";
  public static final String SLASH = "/";
  public static final int MAX_RETRY_COUNT = 3;
  public static final String S3_IN_PATH = S3_ROOT_FOLDER + S3_IN + SLASH;
  public static final String S3_OUT_PATH = S3_ROOT_FOLDER + S3_OUT + SLASH;
  public static final String S3_PROCESSED_PATH = S3_ROOT_FOLDER + S3_PROCESSED + SLASH;
  public static final String UPLOAD_ERROR = "Não foi possível fazer o upload do arquivo";
  public static final String FILE_NOT_FOUND = "Nenhum arquivo encontrado";
  public static final String EMPTY_FOLDER = "O arquivo não possui nome ou, a pasta está vazia";
  public static final String TO_READ_FILE = " para ler o arquivo";
  public static final String S3_ERROR = "Erro ao tentar tentar conectar com o servidor s3";
}
