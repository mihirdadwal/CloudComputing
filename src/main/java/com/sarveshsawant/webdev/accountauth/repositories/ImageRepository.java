package com.sarveshsawant.webdev.accountauth.repositories;

import com.sarveshsawant.webdev.accountauth.domain.entities.ImageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ImageRepository extends CrudRepository<ImageEntity, UUID> {
    ImageEntity findByUserId(UUID userId);
}
