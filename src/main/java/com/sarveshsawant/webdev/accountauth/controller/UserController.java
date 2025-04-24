package com.sarveshsawant.webdev.accountauth.controller;

import com.sarveshsawant.webdev.accountauth.domain.dto.RequestUserPostDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.RequestUserUpdateDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseImageDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseUserDto;
import com.sarveshsawant.webdev.accountauth.service.StorageService;
import com.sarveshsawant.webdev.accountauth.service.UserService;
import com.sarveshsawant.webdev.exception.custom.FileEmptyException;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(path = "/v1/user")
@Slf4j
public class UserController {

    UserService userService;
    StorageService storageService;

    UserController(UserService userService, StorageService storageService, MeterRegistry meterRegistry) {
        this.userService = userService;
        this.storageService = storageService;
        log.info("UserController initialized");
    }

    @GetMapping(path = "/self")
    public ResponseEntity<ResponseUserDto> getUser(@AuthenticationPrincipal User user,@RequestBody(required = false) String body,
                                                   @RequestParam Map<String, String> queryParams) throws BadRequestException {
        log.info("Received request to get user details for username: {}", user.getUsername());
        // Check if body is present
        if (body != null && !body.isEmpty()) {
            throw new BadRequestException();
        }
        // Check if query params are passed
        if (!queryParams.isEmpty()) {
            throw new BadRequestException();
        }
        ResponseUserDto responseUserDto = userService.getUser(user.getUsername());
        log.info("Successfully retrieved user details for username: {}", user.getUsername());

        return new ResponseEntity<>(responseUserDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseUserDto> createUser(@RequestBody @Valid RequestUserPostDto requestUserPostDto,
                                                      @RequestParam Map<String, String> queryParams) throws BadRequestException{
        log.info("Received request to create new user");
        // Check if query params are passed
        if (!queryParams.isEmpty()) {
            throw new BadRequestException();
        }
        ResponseUserDto responseUserDto = userService.createUser(requestUserPostDto);
        log.info("Successfully created new user");

        return new ResponseEntity<>(responseUserDto, HttpStatus.CREATED);
    }

    @PutMapping(path = "/self")
    public ResponseEntity<Void> fullUpdateUser(@AuthenticationPrincipal User user,
                                               @RequestBody @Valid RequestUserUpdateDto requestUserUpdateDto,
                                               @RequestParam Map<String, String> queryParams) throws BadRequestException{
        log.info("Received request to update user details for username: {}", user.getUsername());
        if (requestUserUpdateDto.firstName() == null && requestUserUpdateDto.lastName() == null && requestUserUpdateDto.password() == null) {
            throw new BadRequestException();
        }
        if (!queryParams.isEmpty()) {
            throw new BadRequestException();
        }
        userService.fullUpdateUser(user.getUsername(), requestUserUpdateDto);
        log.info("Successfully updated user details for username: {}", user.getUsername());

        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/self/pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseImageDto> uploadFile(@RequestParam("profilePic") MultipartFile file, @AuthenticationPrincipal User user) {
        log.info("Received request to upload profile picture for username: {}", user.getUsername());
        if(file == null || file.isEmpty()) {
            throw new FileEmptyException("file is empty");
        }
        try {
            ResponseImageDto responseImageDto = storageService.save(file, user.getUsername());
            log.info("Successfully uploaded profile picture for username: {}", user.getUsername());
            return new ResponseEntity<>(responseImageDto, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(path = "/self/pic")
    public ResponseEntity<Void> deleteFile(@AuthenticationPrincipal User user, @RequestBody(required = false) String body,
                                           @RequestParam Map<String, String> queryParams) {
        log.info("Received request to delete profile picture for username: {}", user.getUsername());
        if (!queryParams.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (body != null && !body.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        storageService.delete(user.getUsername());
        log.info("Successfully deleted profile picture for username: {}", user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/self/pic")
    public ResponseEntity<ResponseImageDto> retrieveFile(@AuthenticationPrincipal User user, @RequestBody(required = false) String body,
                                                         @RequestParam Map<String, String> queryParams) {
        log.info("Received request to retrieve profile picture for username: {}", user.getUsername());
        if (!queryParams.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (body != null && !body.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ResponseImageDto responseImageDto = storageService.retrieve(user.getUsername());
        log.info("Successfully retrieved profile picture for username: {}", user.getUsername());
        return ResponseEntity.ok(responseImageDto);
    }
}