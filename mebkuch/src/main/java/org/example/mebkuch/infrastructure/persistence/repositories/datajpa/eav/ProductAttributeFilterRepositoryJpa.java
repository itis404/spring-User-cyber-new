package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav;

import org.example.mebkuch.infrastructure.persistence.entities.eav.ProductFilterIndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductAttributeFilterRepositoryJpa extends JpaRepository<ProductFilterIndexEntity, Long>,
        JpaSpecificationExecutor<ProductFilterIndexEntity> {
}