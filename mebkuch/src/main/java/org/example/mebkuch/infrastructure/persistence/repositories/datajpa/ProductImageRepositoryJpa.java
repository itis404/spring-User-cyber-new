package org.example.mebkuch.infrastructure.persistence.repositories.datajpa;

import org.example.mebkuch.infrastructure.persistence.entities.image.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepositoryJpa extends JpaRepository<ProductImageEntity, Long> {

    List<ProductImageEntity> findByProductId(Long productId);
}