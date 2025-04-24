package com.sarveshsawant.webdev.accountauth.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record RequestUserPostDto(
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

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
                message = "Password must be 8-20 characters long and contain at least one digit, one uppercase letter, one lowercase letter, and one special character"
        )
        String password,

        @JsonProperty("account_created")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String accountCreateDate,

        @JsonProperty("account_updated")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String accountUpdateDate
) {}
