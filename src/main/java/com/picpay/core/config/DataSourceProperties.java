package com.picpay.core.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DataSourceProperties {
  private final String poolName;
  private final String url;
  private final String username;
  private final String password;
  private final int connectionTimeout;
  private final int minimumIdle;
  private final int maximumPoolSize;
  private final int idleTimeout;
  private final int maxLifetime;
  private final String driverClassName;

  public DataSourceProperties(
      @Value("${spring.application.name}") final String poolName,
      @Value("${spring.datasource.url}") final String url,
      @Value("${spring.datasource.username}") final String username,
      @Value("${spring.datasource.password}") final String password,
      @Value("${spring.datasource.hikari.connection-timeout}") final int connectionTimeout,
      @Value("${spring.datasource.hikari.minimum-idle}") final int minimumIdle,
      @Value("${spring.datasource.hikari.maximum-pool-size}") final int maximumPoolSize,
      @Value("${spring.datasource.hikari.idle-timeout}") final int idleTimeout,
      @Value("${spring.datasource.hikari.max-lifetime}") final int maxLifetime,
      @Value("${spring.datasource.driver-class-name}") final String driverClassName) {
    this.poolName = poolName;
    this.url = url;
    this.username = username;
    this.password = password;
    this.connectionTimeout = connectionTimeout;
    this.minimumIdle = minimumIdle;
    this.maximumPoolSize = maximumPoolSize;
    this.idleTimeout = idleTimeout;
    this.maxLifetime = maxLifetime;
    this.driverClassName = driverClassName;
  }
}
