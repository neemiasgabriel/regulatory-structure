package com.picpay.core.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SftpProperties {
  private final String host;
  private final int port;
  private final String username;
  private final String password;
  private final String remoteDir;
  private final int timeout;

  public SftpProperties(
      @Value("${sftp.host}") final String host,
      @Value("${sftp.port}") final int port,
      @Value("${sftp.username}") final String username,
      @Value("${sftp.password}") final String password,
      @Value("${sftp.remote-dir}") final String remoteDir,
      @Value("${sftp.timeout}") final int timeout) {
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
    this.remoteDir = remoteDir;
    this.timeout = timeout;
  }

  @Override
  public String toString() {
    return host + ":" + port + "/" + remoteDir;
  }
}
