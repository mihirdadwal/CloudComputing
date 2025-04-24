package com.sarveshsawant.webdev.accountauth.service.implementation;

import com.sarveshsawant.webdev.accountauth.configuration.AwsS3BucketProperties;
import com.sarveshsawant.webdev.accountauth.domain.dto.RequestImageDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseImageDto;
import com.sarveshsawant.webdev.accountauth.domain.entities.ImageEntity;
import com.sarveshsawant.webdev.accountauth.domain.entities.UserEntity;
import com.sarveshsawant.webdev.accountauth.repositories.ImageRepository;
import com.sarveshsawant.webdev.accountauth.repositories.UserRepository;
import com.sarveshsawant.webdev.accountauth.service.StorageService;
import com.sarveshsawant.webdev.exception.custom.FileFormatInvalidException;
import com.sarveshsawant.webdev.exception.custom.FileSizeExceedException;
import com.sarveshsawant.webdev.exception.custom.ImageAlreadyExists;
import com.sarveshsawant.webdev.exception.custom.ImageNotFoundException;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AwsS3BucketProperties.class)
public class StorageServiceImplementation implements StorageService {

    private final S3Template s3Template;
    private final AwsS3BucketProperties awsS3BucketProperties;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    public ResponseImageDto save(MultipartFile file, String username) throws IOException {

        if (file.isEmpty() || file == null) {
            throw new FileNotFoundException("File is empty");
        }
        if(!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png") && !file.getContentType().equals("image/jpg")) {
            throw new FileFormatInvalidException("File type not supported");
        }
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new FileSizeExceedException("File size exceeds 10MB");
        }
        Optional<UserEntity> userEntity = userRepository.findByEmailAddress(username);
        if(imageRepository.findByUserId(userEntity.get().getId()) != null) {
            throw new ImageAlreadyExists("Image already exists delete it first");
        }
        var objectKey = userEntity.get().getId() + "/" + file.getOriginalFilename();
        var bucketName = awsS3BucketProperties.getBucketName();
        S3Resource result = s3Template.upload(bucketName, objectKey, file.getInputStream());
        RequestImageDto requestImageDto = new RequestImageDto(file.getOriginalFilename(), result.getLocation().getBucket() + "/" + userEntity.get().getId() + "/" + file.getOriginalFilename(), userEntity.get().getId());
        ImageEntity imageEntity = new ImageEntity();
        Optional.ofNullable(requestImageDto.fileName())
                .ifPresent(imageEntity::setFileName);
        Optional.ofNullable(requestImageDto.url())
                .ifPresent(imageEntity::setUrl);
        Optional.ofNullable(requestImageDto.userId())
                .ifPresent(imageEntity::setUserId);
        imageRepository.save(imageEntity);
        ImageEntity imageEntityAfterSave = imageRepository.findByUserId(userEntity.get().getId());
        if(imageEntityAfterSave == null) {
            throw new ImageNotFoundException("Image not found");
        }
        ResponseImageDto responseImageDto = new ResponseImageDto(imageEntityAfterSave.getId(), imageEntityAfterSave.getFileName(), imageEntityAfterSave.getUrl(), imageEntityAfterSave.getImageUploadDate(), imageEntityAfterSave.getUserId());
        return responseImageDto;
    }

    public void delete(String username) {
        Optional<UserEntity> userEntity = userRepository.findByEmailAddress(username);

        ImageEntity imageEntity = imageRepository.findByUserId(userEntity.get().getId());
        if (imageEntity == null) {
            throw new ImageNotFoundException("Image not found");
        }

        imageRepository.delete(imageEntity);
        var bucketName = awsS3BucketProperties.getBucketName();
        s3Template.deleteObject(bucketName, userEntity.get().getId() + "/" + imageEntity.getFileName());
    }

    public ResponseImageDto retrieve(String username) {
        Optional<UserEntity> userEntity = userRepository.findByEmailAddress(username);
        ImageEntity imageEntity = imageRepository.findByUserId(userEntity.get().getId());
        if (imageEntity == null) {
            throw new ImageNotFoundException("Image not found");
        }
        ResponseImageDto responseImageDto = new ResponseImageDto(imageEntity.getId(), imageEntity.getFileName(), imageEntity.getUrl(), imageEntity.getImageUploadDate(), imageEntity.getUserId());
        return responseImageDto;
    }


}
