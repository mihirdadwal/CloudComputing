package com.sarveshsawant.webdev.accountauth.controller;

import com.sarveshsawant.webdev.accountauth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(path = "/v1/verify")
@Slf4j
public class VerifyController {

    UserService userService;

    VerifyController(UserService userService) {
        this.userService = userService;
        log.info("VerifyController initialized");
    }

    @GetMapping
    public ResponseEntity<Void> verifyUser(@RequestParam("email") String email,
                                           @RequestParam("token") String token,
                                           HttpServletRequest request) {
        log.info("Received request to verify user with email: {}", email);
        if (request.getContentLength() > 0) {
            log.warn("Request body is not allowed for this GET endpoint");
            return ResponseEntity.badRequest().build();
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<String> allowedParams = Set.of("email", "token");

        for (String paramName : parameterMap.keySet()) {
            if (!allowedParams.contains(paramName)) {
                log.warn("Unexpected parameter received: {}", paramName);
                return ResponseEntity.badRequest().build();
            }
        }
        if (email == null || token == null) {
            log.warn("Email or token is null");
            return ResponseEntity.badRequest().build();
        }
        if (email.trim().isEmpty() || token.trim().isEmpty()) {
            log.warn("Email or token is empty");
            return ResponseEntity.badRequest().build();
        }
        userService.verifyUser(token, email);
        return ResponseEntity.noContent().build();
    }
}
