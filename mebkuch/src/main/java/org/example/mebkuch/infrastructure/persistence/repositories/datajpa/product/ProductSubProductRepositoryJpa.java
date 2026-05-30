package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product;

import org.example.mebkuch.infrastructure.persistence.entities.products.ProductSubProductEntity;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductSubProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSubProductRepositoryJpa
        extends JpaRepository<ProductSubProductEntity, ProductSubProductId> {

    List<ProductSubProductEntity> findByProduct_Id(Long productId);

    List<ProductSubProductEntity> findBySubProduct_Id(Long subProductId);

    void deleteByProduct_IdAndSubProduct_Id(Long productId, Long subProductId);
}