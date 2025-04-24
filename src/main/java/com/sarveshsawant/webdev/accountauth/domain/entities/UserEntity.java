package com.sarveshsawant.webdev.accountauth.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "person")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "person_id", updatable = false)
    private UUID id;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter valid email")
    @Column(name = "email", unique = true, length = 255, updatable = false)
    private String emailAddress;

    @Column(name = "password")
    @NotBlank(message = "Password is required")
    private String password;

    @Column(name = "first_name")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Field must contain only alphanumeric characters")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Column(name = "last_name")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Field must contain only alphanumeric characters")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Column(name = "account_updated")
    @UpdateTimestamp
    private Date accountUpdateDate;

    @Column(name = "account_created", updatable = false)
    @CreationTimestamp
    private Date accountCreateDate;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;
}
