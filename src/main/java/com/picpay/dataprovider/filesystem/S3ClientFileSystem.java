package com.picpay.dataprovider.filesystem;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.picpay.core.common.helper.Constants;
import com.picpay.core.common.helper.LogGenerator;
import com.picpay.core.config.AwsProperties;
import com.picpay.core.validator.PatternValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.picpay.core.domain.FileInfo;
import com.picpay.core.exception.FileReadingException;
import com.picpay.core.exception.InternalServerException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public final class S3ClientFileSystem implements BucketClient {
  private final S3Client awsClient;
  private final AwsProperties properties;
  private final LogGenerator logGenerator;
  private final PatternValidator patternValidator;

  @Override
  public InputStream readFile(final String filePath, final String objectKey)
      throws FileReadingException, InternalServerException {
    final var fullPath = filePath + Constants.SLASH + objectKey;

    try {
      final var request = GetObjectRequest.builder()
          .bucket(properties.getBucket())
          .key(fullPath)
          .build();

      return awsClient.getObjectAsBytes(request).asInputStream();
    } catch (AmazonServiceException | S3Exception e) {
      final var message = logGenerator.errorMsg(objectKey, "Erro ao ler arquivo '" + fullPath + "'", null);
      log.error(message, e);
      throw new FileReadingException(message);
    } catch (SdkClientException e) {
      final var message = logGenerator.errorMsg(objectKey, Constants.S3_ERROR + Constants.TO_READ_FILE + " '" + fullPath + "'", null);
      log.error(message, e);
      throw new InternalServerException(message);
    }
  }

  @Override
  public void deleteFileFrom(final String srcFolder, final String fileName) {
    final var fullPath = srcFolder + fileName;

    try {
      final var request = DeleteObjectRequest.builder()
          .bucket(properties.getBucket())
          .key(fullPath)
          .build();

      awsClient.deleteObject(request);
    } catch (AmazonServiceException | S3Exception e) {
      log.info(logGenerator.errorMsg(fileName, "Não foi possível deletar o arquivo " + fullPath, e));
    } catch (SdkClientException e) {
      log.error(logGenerator.errorMsg(fileName, Constants.S3_ERROR, e));
    }
  }

  @Override
  public void copyFileTo(final String srcFolder, final String fileName, final String targetFolder) {
    final var bucketName = properties.getBucket();
    final var srcPath = srcFolder + fileName;
    final var targetPath = targetFolder + fileName;

    try {
      final var copyObjRequest = CopyObjectRequest.builder()
          .sourceBucket(bucketName)
          .sourceKey(srcPath)
          .destinationBucket(bucketName)
          .destinationKey(targetPath)
          .build();

      awsClient.copyObject(copyObjRequest);
    } catch (AmazonServiceException | S3Exception e) {
      final var message = "Não foi possível copiar o arquivo de " + srcPath + " para " + targetPath;
      log.info(logGenerator.errorMsg(fileName, message, e), e);
    } catch (SdkClientException e) {
      log.error(logGenerator.errorMsg(fileName, Constants.S3_ERROR, e));
    }
  }

  @Override
  public void moveFile(final String srcFolder, final String fileName, final String targetFolder) {
    copyFileTo(srcFolder, fileName, targetFolder);
    deleteFileFrom(srcFolder, fileName);
  }

  @Override
  public void moveFilesToOut() {
    try {
      final var bucketName = properties.getBucket();
      final var objectRequest = ListObjectsV2Request.builder()
          .bucket(bucketName)
          .prefix(Constants.S3_IN_PATH)
          .build();

      final var fileList = awsClient.listObjectsV2(objectRequest).contents();

      log.info("bucketName: " + bucketName + " | Lista de Arquivos Pasta IN: " + fileList);

      for (final var file : fileList) {
        final var key = file.key().replace(Constants.S3_IN_PATH, "").trim();

        if (key.isEmpty() || key.isBlank()) {
          log.info(Constants.EMPTY_FOLDER);
          continue;
        }

        log.info(logGenerator.logMsg(key, "Arquivo Pasta IN; Tamanho: " + file.size()));

        final var hasPattern = patternValidator.hasFilePattern(key);

        if (hasPattern) {
          moveFile(Constants.S3_IN_PATH, key, Constants.S3_OUT_PATH);
        } else {
          log.error(logGenerator.errorMsg(key, "O arquivo não está no padrão definido para aplicação", null));
        }
      }
    } catch (AmazonServiceException | S3Exception e) {
      log.info(Constants.FILE_NOT_FOUND, e);
    } catch (SdkClientException e) {
      final String message = Constants.S3_ERROR + Constants.TO_READ_FILE;
      log.error(message, e);
      throw new InternalServerException(message);
    }
  }

  @Override
  public List<FileInfo> readAllFiles(final String filePath) throws FileReadingException, InternalServerException {
    try {
      final var bucketName = properties.getBucket();
      final var objectRequest = ListObjectsV2Request
          .builder()
          .bucket(bucketName)
          .prefix(filePath)
          .build();

      final var fileList = awsClient.listObjectsV2(objectRequest).contents();

      log.info("Path: " + filePath + " | Lista de Arquivos: " + fileList.stream().map(S3Object::key).toList());

      final var fileInfos = new ArrayList<FileInfo>();

      for (final var fileData : fileList) {
        final var key = fileData.key();

        if (key == null) {
          continue;
        }

        final var request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        final var data = awsClient.getObjectAsBytes(request);
        final var name = key.replace(filePath, "");

        if (name.endsWith(".txt") && patternValidator.hasFilePattern(name)) {
          final var stream = new InputStreamReader(data.asInputStream(), UTF_8);
          fileInfos.add(new FileInfo(name, stream));
          log.info(logGenerator.logMsg(name, "Arquivo adicionado à lista; Tamanho: " + fileData.size()));
        }
      }

      return fileInfos;
    } catch (AmazonServiceException | S3Exception e) {
      final String message = "Nenhum arquivo encontrado em '" + filePath + "'";
      log.info(message, e);
      return Collections.emptyList();
    } catch (SdkClientException e) {
      final var message = Constants.S3_ERROR + Constants.TO_READ_FILE + " '" + filePath + "'";
      log.error(message, e);
      throw new InternalServerException(message);
    }
  }

  @Override
  public void copyFile(final String srcFolder, final String objectKey, final String targetFolder)
      throws FileReadingException, InternalServerException {
    try {
      final var bucketName = properties.getBucket();
      final var source = srcFolder + Constants.SLASH + objectKey;
      final var target = targetFolder + Constants.SLASH + objectKey;
      final var copyRequest = CopyObjectRequest.builder()
          .sourceBucket(bucketName)
          .sourceKey(source)
          .destinationBucket(bucketName)
          .destinationKey(target)
          .build();

      awsClient.copyObject(copyRequest);
      log.info(logGenerator.logMsg(objectKey, "Arquivo copiado para pasta " + targetFolder));
    } catch (AmazonServiceException | S3Exception e) {
      final var message = logGenerator.errorMsg(objectKey, "Erro ao copiar arquivo de '" + srcFolder + "'", null);
      log.info(message, e);
      throw new FileReadingException(message);
    } catch (SdkClientException e) {
      final var message = logGenerator.errorMsg(objectKey, Constants.S3_ERROR + " para copiar o arquivo de '" + srcFolder + "'", null);
      log.error(message, e);
      throw new InternalServerException(message);
    }
  }

  @Override
  public boolean uploadFile(final String fileName, final String filePath)
      throws FileReadingException, InternalServerException {
    final var bucketPath = filePath + Constants.SLASH + fileName;
    final var localPath = Paths.get(fileName);

    log.info("Bucket path: " + bucketPath);
    log.info("Local file path: " + localPath);

    try {
      final var request = PutObjectRequest.builder()
          .bucket(properties.getBucket())
          .key(bucketPath)
          .build();

      awsClient.putObject(request, localPath);

      return true;
    } catch (AmazonServiceException | S3Exception e) {
      final String message = logGenerator.errorMsg(fileName, errorMsg(bucketPath), null);
      log.info(message, e);
      throw new FileReadingException(message);
    } catch (SdkClientException e) {
      final String message = logGenerator.errorMsg(fileName, Constants.S3_ERROR + Constants.TO_READ_FILE + " '" + bucketPath + "'", null);
      log.error(message, e);
      throw new InternalServerException(message);
    }
  }

  private static String errorMsg(final String fullPath) {
    return "Erro ao tentar subir o arquivo para o S3 '" + fullPath + "'";
  }

  @Override
  public void uploadFileFromRequest(final MultipartFile multipartFile) {
    final var file = Constants.S3_IN_PATH + multipartFile.getOriginalFilename();

    try {
      final var is = multipartFile.getInputStream();
      final var request = PutObjectRequest.builder()
          .bucket(properties.getBucket())
          .key(file)
          .build();

      awsClient.putObject(request, RequestBody.fromInputStream(is, is.available()));
      log.info(logGenerator.logMsg(multipartFile.getName(), "Upload para S3 finalizado..."));
    } catch (AmazonServiceException | S3Exception | IOException e) {
      log.info(logGenerator.errorMsg(multipartFile.getName(), Constants.UPLOAD_ERROR, e));
      throw new FileReadingException(Constants.UPLOAD_ERROR);
    } catch (SdkClientException e) {
      log.info(logGenerator.errorMsg(multipartFile.getName(), Constants.UPLOAD_ERROR, e));
      throw new InternalServerException(Constants.UPLOAD_ERROR);
    }
  }
}
