package com.sarveshsawant.webdev.accountauth.service.implementation;

import com.sarveshsawant.webdev.accountauth.domain.entities.UserEntity;
import com.sarveshsawant.webdev.accountauth.repositories.UserRepository;
import com.sarveshsawant.webdev.dbhealthcheck.service.DatabaseHealthCheckService;
import com.sarveshsawant.webdev.exception.custom.EmailNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UsernameAndPassForAuth implements UserDetailsService {

    UserRepository userRepository;


    UsernameAndPassForAuth(UserRepository userRepository){
        this.userRepository = userRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmailAddress(username)
                .orElseThrow(() -> new EmailNotFoundException("User not found with email: " + username));

        return new User(userEntity.getEmailAddress(), userEntity.getPassword(), Collections.emptyList());
    }
}
