package com.picpay.core.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AwsProperties {
  private final String region;
  private final String bucket;

  public AwsProperties(
      @Value("${spring.cloud.aws.bucket.region}") final String region,
      @Value("${spring.cloud.aws.bucket.bucketName}") final String bucket) {
    this.region = region;
    this.bucket = bucket;
  }
}
