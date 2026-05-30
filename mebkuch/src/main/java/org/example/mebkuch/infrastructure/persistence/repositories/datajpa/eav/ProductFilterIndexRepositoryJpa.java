package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav;

import org.example.mebkuch.infrastructure.persistence.entities.eav.ProductFilterIndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFilterIndexRepositoryJpa extends JpaRepository<ProductFilterIndexEntity, Long> {

    void deleteByProductId(Long productId);

}