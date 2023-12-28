package com.picpay.core.domain;

import com.picpay.core.enums.CorpEnum;
import com.picpay.core.enums.GroupsEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStreamReader;

@Getter
@Setter
@NoArgsConstructor
public final class FileInfo {
  private CorpEnum corpName;
  private String fileName;
  private String date;
  private String timestamp;
  private InputStreamReader fileStreamReader;

  public FileInfo(final String fileName, final InputStreamReader fileStreamReader) {
    final var groups = fileName.split("_");

    this.fileName = fileName;
    this.fileStreamReader = fileStreamReader;
    this.corpName = CorpEnum.getCorpEnum(groups[GroupsEnum.ONE.number()]);
    this.date = groups[GroupsEnum.TWO.number()];
    this.timestamp = groups[GroupsEnum.THREE.number()].replace(".txt", "");
  }
}
