package com.sarveshsawant.webdev.accountauth.service;


import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    ResponseImageDto save(MultipartFile file, String username) throws IOException;
    void delete(String username);
    ResponseImageDto retrieve(String username);
}