package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav;

import org.example.mebkuch.infrastructure.persistence.entities.eav.ProductAttributeEntity;
import org.example.mebkuch.infrastructure.persistence.entities.eav.ProductAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeRepositoryJpa
        extends JpaRepository<ProductAttributeEntity, ProductAttributeId> {

    void deleteByProductId(Long productId);

    void deleteByProductIdAndAttributeValueId(Long productId, Long attributeValueId);

    List<ProductAttributeEntity> findByProductId(Long productId);
}