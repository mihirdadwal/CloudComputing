package com.sarveshsawant.webdev.accountauth.domain.dto;

import jakarta.persistence.Column;

import java.util.UUID;

public record RequestImageDto(

        @Column(name = "file_name")
    String fileName,

        @Column(name = "url")
    String url,

    @Column(name = "user_id")
    UUID userId
) {}
