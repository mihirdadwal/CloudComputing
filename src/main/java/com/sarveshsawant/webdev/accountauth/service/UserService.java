package com.sarveshsawant.webdev.accountauth.service;

import com.sarveshsawant.webdev.accountauth.domain.dto.RequestUserPostDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.RequestUserUpdateDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseUserDto;

public interface UserService {
    ResponseUserDto getUser(String emailAddress);
    ResponseUserDto createUser(RequestUserPostDto requestUserPostDto);
    void fullUpdateUser(String emailAddress, RequestUserUpdateDto requestUserUpdateDto);
    void verifyUser(String token, String emailAddress);

    boolean isUserAlreadyVerified(String emailAddress);
}
