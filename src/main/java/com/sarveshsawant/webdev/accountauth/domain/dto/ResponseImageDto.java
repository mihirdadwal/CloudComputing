package com.sarveshsawant.webdev.accountauth.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.UUID;

public record ResponseImageDto (
    @JsonProperty("id")
    @Id
    UUID id,

    @JsonProperty("file_name")
    @Column(name = "file_name")
    String fileName,

    @Column(name = "url")
    String url,

    @JsonProperty("upload_date")
    @Column(name = "upload_date")
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate accountUpdateDate,

    @JsonProperty("user_id")
    @Column(name = "user_id")
    UUID userId
) {}