package com.picpay.dataprovider.filesystem;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.picpay.core.common.helper.Constants;
import com.picpay.core.common.helper.LogGenerator;
import com.picpay.core.config.MiniIoProperties;
import com.picpay.core.validator.PatternValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.picpay.core.domain.FileInfo;
import com.picpay.core.exception.FileReadingException;
import com.picpay.core.exception.InternalServerException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public class MiniIoClientFileSystem implements BucketClient {
  private final AmazonS3 ioClient;
  private final MiniIoProperties properties;
  private final LogGenerator logGenerator;
  private final PatternValidator patternValidator;

  @Override
  public InputStream readFile(final String filePath, final String objectKey)
      throws FileReadingException, InternalServerException {
    final var fullPath = filePath + Constants.SLASH + objectKey;
    try {
      final var objectRequest = new GetObjectRequest(properties.getBucket(), fullPath);
      return ioClient.getObject(objectRequest).getObjectContent().getDelegateStream();
    } catch (AmazonServiceException e) {
      final var message = logGenerator.errorMsg(objectKey, "Erro ao ler arquivo '" + fullPath + "'", null);
      log.info(message, e);
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
      ioClient.deleteObject(properties.getBucket(), fullPath);
    } catch (AmazonServiceException e) {
      log.info(logGenerator.errorMsg(fileName, "Não foi possível deletar o arquivo " + fullPath, e));
    } catch (SdkClientException e) {
      log.error(logGenerator.errorMsg(fileName, Constants.S3_ERROR, e));
    }
  }

  @Override
  public void copyFileTo(final String srcFolder, final String fileName, final String targetFolder) {
    final var srcPath = srcFolder + fileName;
    final var targetPath = targetFolder + fileName;
    final var bucketName = properties.getBucket();

    try {
      final var copyObjRequest = new CopyObjectRequest(bucketName, srcPath, bucketName, targetPath);
      ioClient.copyObject(copyObjRequest);
    } catch (AmazonServiceException e) {
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
    final var bucketName = properties.getBucket();

    try {
      final var objectRequest = new ListObjectsV2Request()
          .withBucketName(bucketName)
          .withPrefix(Constants.S3_IN_PATH);

      final var fileList = ioClient.listObjectsV2(objectRequest).getObjectSummaries();

      for (final var file : fileList) {
        final var key = file.getKey().replace(Constants.S3_IN_PATH, "").trim();

        if (key.isEmpty() || key.isBlank()) {
          log.info(Constants.EMPTY_FOLDER);
          continue;
        }

        log.info(logGenerator.logMsg(key, "Arquivo Pasta IN; Tamanho: " + file.getSize()));

        final var hasPattern = patternValidator.hasFilePattern(key);

        if (hasPattern) {
          moveFile(Constants.S3_IN_PATH, key, Constants.S3_OUT_PATH);
        } else {
          log.error(logGenerator.errorMsg(key, "O arquivo não está no padrão definido para aplicação", null));
        }
      }
    } catch (AmazonServiceException e) {
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
      final var objectRequest = new ListObjectsV2Request()
          .withBucketName(bucketName)
          .withPrefix(filePath);

      final var fileList = ioClient.listObjectsV2(objectRequest).getObjectSummaries();

      return fileList.stream().map(file -> {
            final var data = ioClient.getObject(new GetObjectRequest(bucketName, file.getKey()));
            final var name = data.getKey().replace(filePath, "");

            if (name.endsWith(".txt") && patternValidator.hasFilePattern(name)) {
              final var stream = new InputStreamReader(data.getObjectContent().getDelegateStream(), UTF_8);
              log.info(logGenerator.logMsg(name, "Arquivo adicionado à lista; Tamanho: " + file.getSize()));
              return new FileInfo(name, stream);
            }

            return null;
          }).filter(Objects::nonNull)
          .toList();
    } catch (AmazonServiceException e) {
      final String message = "Nenhum arquivo encontrado em '" + filePath + "'";
      log.info(message, e);
      return Collections.emptyList();
    } catch (SdkClientException e) {
      final String message = Constants.S3_ERROR + " para ler o arquivo '" + filePath + "'";
      log.error(message, e);
      throw new InternalServerException(message);
    }
  }

  @Override
  public void copyFile(String srcFolder, String objectKey, String targetFolder)
      throws FileReadingException, InternalServerException {
    try {
      final var bucketName = properties.getBucket();
      final var source = srcFolder + Constants.SLASH + objectKey;
      final var target = targetFolder + Constants.SLASH + objectKey;
      final var copyObjRequest = new CopyObjectRequest(bucketName, source, bucketName, target);
      ioClient.copyObject(copyObjRequest);
      log.info(logGenerator.logMsg(objectKey, "Arquivo copiado para pasta " + targetFolder));
    } catch (AmazonServiceException e) {
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
      final var request = new PutObjectRequest(properties.getBucket(), bucketPath, new File(fileName));
      ioClient.putObject(request);
      return true;
    } catch (AmazonServiceException e) {
      final String message = logGenerator.errorMsg(fileName, errorMsg(bucketPath), null);
      log.info(message, e);
      throw new FileReadingException(message);
    } catch (SdkClientException e) {
      final String message = logGenerator.errorMsg(fileName, Constants.S3_ERROR + Constants.TO_READ_FILE + " '" + bucketPath + "'", null);
      log.error(message, e);
      throw new InternalServerException(message);
    }
  }

  private static String errorMsg(String fullPath) {
    return "Erro ao tentar subir o arquivo para o S3 '" + fullPath + "'";
  }

  @Override
  public void uploadFileFromRequest(final MultipartFile multipartFile) {
    final var file = Constants.S3_IN_PATH + multipartFile.getOriginalFilename();

    try {
      final var fileData = new File("src/main/resources/" + multipartFile.getOriginalFilename());
      final var request = new PutObjectRequest(properties.getBucket(), file, fileData);

      ioClient.putObject(request);
      log.info(logGenerator.logMsg(multipartFile.getName(), "Upload para S3 finalizado..."));
    } catch (AmazonServiceException | S3Exception e) {
      log.info(logGenerator.errorMsg(multipartFile.getName(), Constants.UPLOAD_ERROR, e));
      throw new FileReadingException(Constants.UPLOAD_ERROR);
    } catch (SdkClientException e) {
      log.info(logGenerator.errorMsg(multipartFile.getName(), Constants.UPLOAD_ERROR, e));
      throw new InternalServerException(Constants.UPLOAD_ERROR);
    }
  }
}