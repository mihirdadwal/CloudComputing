package com.sarveshsawant.webdev.accountauth.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarveshsawant.webdev.accountauth.domain.dto.RequestUserPostDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.RequestUserUpdateDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseUserDto;
import com.sarveshsawant.webdev.accountauth.domain.entities.ImageEntity;
import com.sarveshsawant.webdev.accountauth.domain.entities.UserEntity;
import com.sarveshsawant.webdev.accountauth.repositories.ImageRepository;
import com.sarveshsawant.webdev.accountauth.repositories.UserRepository;
import com.sarveshsawant.webdev.accountauth.service.UserService;
import com.sarveshsawant.webdev.exception.custom.*;
import io.awspring.cloud.sns.core.SnsTemplate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private SnsTemplate snsTemplate;
    @Value("${aws.sns.topic.arn}")
    private String snsTopicArn;
    private ObjectMapper objectMapper;



    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, SnsTemplate snsTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.snsTemplate = snsTemplate;
    }

    @Override
    public ResponseUserDto getUser(@Email String emailAddress) {
        UserEntity userEntity = findUserEntityByEmail(emailAddress);
        ResponseUserDto responseUserDto = mapResponseEntityToResponseUserDto(userEntity);
        return responseUserDto;
    }

    @Override
    public ResponseUserDto createUser(RequestUserPostDto requestUserPostDto) {
        UserEntity userEntity = new UserEntity();
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(2);

        userEntity.setVerificationToken(verificationToken);
        userEntity.setTokenExpiry(expiryTime);
        userEntity.setVerified(false);

        Optional.ofNullable(requestUserPostDto.firstName())
                .ifPresent(userEntity::setFirstName);
        Optional.ofNullable(requestUserPostDto.lastName())
                .ifPresent(userEntity::setLastName);
        Optional.ofNullable(requestUserPostDto.emailAddress())
                .ifPresent(userEntity::setEmailAddress);
        Optional.ofNullable(passwordEncoder.encode(requestUserPostDto.password()))
                .ifPresent(userEntity::setPassword);

        UserEntity savedUser = userRepository.save(userEntity);

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("userId", savedUser.getId().toString());
        messageMap.put("email", savedUser.getEmailAddress());
        messageMap.put("firstName", savedUser.getFirstName());
        messageMap.put("token", verificationToken);
        messageMap.put("expiryTime", expiryTime.toString());


        try {
            snsTemplate.convertAndSend(snsTopicArn, messageMap);
            log.info("Verification email SNS published for user: {}", savedUser.getEmailAddress());
        } catch (Exception e) {
            log.error("Failed to publish SNS message for email verification: {}", e.getMessage());
            throw new VerificationException("Failed to initiate email verification");
        }

        ResponseUserDto responseUserDto = mapResponseEntityToResponseUserDto(savedUser);

        return responseUserDto;
    }

    @Override
    public void fullUpdateUser(@Email String emailAddress, RequestUserUpdateDto requestUserUpdateDto) {


        UserEntity userToBeUpdatedEntity = findUserEntityByEmail(emailAddress);

        Optional.ofNullable(requestUserUpdateDto.firstName()).ifPresent(userToBeUpdatedEntity::setFirstName);
        Optional.ofNullable(requestUserUpdateDto.lastName()).ifPresent(userToBeUpdatedEntity::setLastName);
        Optional.ofNullable(requestUserUpdateDto.password()).ifPresent(password -> {
            userToBeUpdatedEntity.setPassword(passwordEncoder.encode(password));
        });

        UserEntity afterSaveUserEntity = userRepository.save(userToBeUpdatedEntity);

    }

    private ResponseUserDto mapResponseEntityToResponseUserDto(UserEntity userEntity){
        ResponseUserDto responseUserDto = new ResponseUserDto(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmailAddress(),
                userEntity.getAccountCreateDate(),
                userEntity.getAccountUpdateDate(),
                userEntity.isVerified()
        );
        return responseUserDto;
    }

    private UserEntity findUserEntityByEmail(String emailAddress){
        return userRepository.findByEmailAddress(emailAddress).orElseThrow(
                () -> new EmailNotFoundException("Email address not found " + emailAddress)
        );
    }

    public void verifyUser(String token, String emailAddress) {
        //Does the email exist in the database
        if(!userExists(emailAddress)) {
            throw new EmailNotFoundException("Email address not found " + emailAddress);
        }

        //Is the email address already verified
        if (isUserAlreadyVerified(emailAddress)) {
            throw new UserAlreadyVerifiedException("User is already verified");
        }

        //Is the token valid
        if (!isValidToken(token)) {
            throw new InvalidTokenException("Token not found exception");
        }

        //Is the email token combination valid
        if(!isUserEmailTokenValid(emailAddress, token)) {
            throw new InvalidTokenException("Invalid verification token");
        }

        //Is the token expired
        if (isTokenExpired(token)) {
            throw new TokenExpiredException("Verification token has expired");
        }

        UserEntity user = userRepository.findByVerificationToken(token).get();
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
    }

    private boolean isTokenExpired(String token) {
        Optional<UserEntity> userEntity = userRepository.findByVerificationToken(token);
        System.out.println("Token expiry: " + userEntity.get().getTokenExpiry());
        return userEntity.map(user ->
                LocalDateTime.now().isAfter(user.getTokenExpiry())
        ).orElse(true);
    }

    private boolean isValidToken(String token) {
        return userRepository.findByVerificationToken(token).isPresent();
    }

    public boolean isUserAlreadyVerified(String emailAddress) {
        Optional<UserEntity> userEntity = userRepository.findByEmailAddress(emailAddress);
        return userEntity.map(UserEntity::isVerified).orElse(false);
    }

    private boolean isUserEmailTokenValid(String emailAddress, String token) {
        Optional<UserEntity> userEntity = userRepository.findByEmailAddress(emailAddress);
        return userEntity.map(user -> user.getVerificationToken().equals(token)).orElse(false);
    }

    public boolean userExists(String emailAddress) {
        return userRepository.findByEmailAddress(emailAddress).isPresent();
    }
}
