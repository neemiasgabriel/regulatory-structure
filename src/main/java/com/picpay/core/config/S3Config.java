package com.picpay.core.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {
  private final AwsProperties properties;
  private final MiniIoProperties ioProperties;

  @Bean("awsClient")
  public S3Client awsClient() {
    return S3Client.builder()
        .region(Region.of(properties.getRegion()))
        .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
        .build();
  }

  @Bean("ioClient")
  public AmazonS3 ioClient() {
    final var ioProvider = new BasicAWSCredentials(ioProperties.getAccessKey(), ioProperties.getSecretKey());

    return AmazonS3Client.builder()
        .withCredentials(new AWSStaticCredentialsProvider(ioProvider))
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ioProperties.getHost(), ioProperties.getRegion()))
        .withPathStyleAccessEnabled(true)
        .build();
  }
}