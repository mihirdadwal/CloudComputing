package com.sarveshsawant.webdev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarveshsawant.webdev.accountauth.domain.dto.RequestUserPostDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.RequestUserUpdateDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseUserDto;
import com.sarveshsawant.webdev.accountauth.domain.entities.UserEntity;
import com.sarveshsawant.webdev.accountauth.repositories.UserRepository;
import com.sarveshsawant.webdev.accountauth.service.implementation.UserServiceImplementation;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private UserServiceImplementation userService;

    @MockBean
    private SnsTemplate snsTemplate;

    private void createAndVerifyUser(RequestUserPostDto postDto) {
        ResponseUserDto responseDto = userService.createUser(postDto);
        UserEntity user = userRepository.findByEmailAddress(postDto.emailAddress()).get();
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
    }

    @BeforeEach
    void setUp() {
        doNothing().when(snsTemplate).convertAndSend(anyString(), any(Map.class));
        userService = new UserServiceImplementation(userRepository, passwordEncoder, modelMapper, snsTemplate, objectMapper);
    }

    @Test
    void testCreateUser() {
        RequestUserPostDto postDto = new RequestUserPostDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "$Sanchita151",
                null,
                null
        );

        ResponseEntity<ResponseUserDto> response = restTemplate.postForEntity("/v1/user", postDto, ResponseUserDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ResponseUserDto responseDto = response.getBody();
        assertNotNull(responseDto);
        assertEquals("John", responseDto.firstName());
        assertEquals("Doe", responseDto.lastName());
        assertEquals("john.doe@example.com", responseDto.emailAddress());
    }

    @Test
    void testGetUser() {
        RequestUserPostDto postDto = new RequestUserPostDto(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "$Sanchita151",
                null,
                null
        );
        createAndVerifyUser(postDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("jane.smith@example.com", "$Sanchita151");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ResponseUserDto> response = restTemplate.exchange(
                "/v1/user/self",
                HttpMethod.GET,
                entity,
                ResponseUserDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseUserDto responseDto = response.getBody();
        assertNotNull(responseDto);
        assertEquals("Jane", responseDto.firstName());
        assertEquals("Smith", responseDto.lastName());
        assertEquals("jane.smith@example.com", responseDto.emailAddress());
    }

    @Test
    void testUpdateUser() {
        RequestUserPostDto postDto = new RequestUserPostDto(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "$Sanchita151",
                null,
                null
        );
        createAndVerifyUser(postDto);

        RequestUserUpdateDto updateDto = new RequestUserUpdateDto("Janet", "Johnson", "$Sanchita151");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("jane.smith@example.com", "$Sanchita151");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestUserUpdateDto> entity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/v1/user/self",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify the update
        HttpEntity<String> getEntity = new HttpEntity<>(null, headers);
        ResponseEntity<ResponseUserDto> getResponse = restTemplate.exchange(
                "/v1/user/self",
                HttpMethod.GET,
                getEntity,
                ResponseUserDto.class
        );

        ResponseUserDto updatedUser = getResponse.getBody();
        assertNotNull(updatedUser);
        assertEquals("Janet", updatedUser.firstName());
        assertEquals("Johnson", updatedUser.lastName());
    }

    @Test
    void testCreateUserWithInvalidData() {
        RequestUserPostDto postDto = new RequestUserPostDto(
                "", // Invalid first name
                "Doe",
                "invalid-email", // Invalid email
                "weak", // Invalid password
                null,
                null
        );

        ResponseEntity<ResponseUserDto> response = restTemplate.postForEntity("/v1/user", postDto, ResponseUserDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetUserWithInvalidCredentials() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("nonexistent@example.com", "wrongpassword");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ResponseUserDto> response = restTemplate.exchange(
                "/v1/user/self",
                HttpMethod.GET,
                entity,
                ResponseUserDto.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateUserWithInvalidData() {
        RequestUserPostDto postDto = new RequestUserPostDto(
                "Jane",
                "Smith",
                "jane.update@example.com",
                "$Sanchita151",
                null,
                null
        );
        createAndVerifyUser(postDto);

        RequestUserUpdateDto updateDto = new RequestUserUpdateDto("", "", "weak");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("jane.update@example.com", "$Sanchita151");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestUserUpdateDto> entity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/v1/user/self",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}