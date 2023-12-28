package com.picpay.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
  private final DataSourceProperties properties;

  @Bean
  public DataSource dataSource() {
    final var config = new HikariConfig();
    config.setPoolName(properties.getPoolName());
    config.setMinimumIdle(properties.getMinimumIdle());
    config.setMaximumPoolSize(properties.getMaximumPoolSize());
    config.setConnectionTimeout(properties.getConnectionTimeout());
    config.setIdleTimeout(properties.getIdleTimeout());
    config.setMaxLifetime(properties.getMaxLifetime());
    config.setDriverClassName(properties.getDriverClassName());
    config.setJdbcUrl(properties.getUrl());
    config.setUsername(properties.getUsername());
    config.setPassword(properties.getPassword());

    return new HikariDataSource(config);
  }
}
