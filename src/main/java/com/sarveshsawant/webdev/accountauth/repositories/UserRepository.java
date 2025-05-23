package com.sarveshsawant.webdev.accountauth.repositories;

import com.sarveshsawant.webdev.accountauth.domain.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmailAddress(String emailAddress);
    Optional<UserEntity> findByVerificationToken(String token);
    Optional<UserEntity> findByEmailAddressAndIsVerified(String emailAddress, boolean isVerified);
}
