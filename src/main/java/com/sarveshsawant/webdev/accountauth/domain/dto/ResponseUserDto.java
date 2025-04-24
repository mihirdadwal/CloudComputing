package com.sarveshsawant.webdev.accountauth.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.UUID;

public record ResponseUserDto(
        @JsonProperty("id")
        UUID id,

        @NotBlank(message = "First name is required")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Field must contain only alphanumeric characters")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @JsonProperty("first_name")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Field must contain only alphanumeric characters")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @JsonProperty("last_name")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter valid email")
        @JsonProperty("email")
        String emailAddress,

        @JsonProperty("account_created")
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        Date accountCreateDate,

        @JsonProperty("account_updated")
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        Date accountUpdateDate,

        @JsonProperty("is_verified")
        boolean isVerified
) {}
