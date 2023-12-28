package com.picpay.core.usecase;

import com.picpay.dataprovider.filesystem.BucketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class FileActionsUseCase {
  // Para utilizar o MiniIoClientFileSystem, basta substituir o nome do atributo para miniIoClientFileSystem
  private final BucketClient s3ClientFileSystem;
}
