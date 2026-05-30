package org.example.mebkuch.infrastructure.persistence.repositories.eav;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.repository.eav.IProductFilterIndexRepository;
import org.example.mebkuch.infrastructure.persistence.entities.eav.ProductFilterIndexEntity;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav.ProductFilterIndexRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;


@Repository
@Primary
@RequiredArgsConstructor
public class ProductFilterIndexRepository implements IProductFilterIndexRepository {

    private final ProductFilterIndexRepositoryJpa productFilterIndexRepositoryJpa;

    @Override
    public void saveIndex(Long productId,
                          Long categoryId,
                          Long attributeId,
                          Long attributeValueId) {

        ProductFilterIndexEntity entity = ProductFilterIndexEntity.builder()
                .productId(productId)
                .categoryId(categoryId)
                .attributeId(attributeId)
                .attributeValueId(attributeValueId)
                .build();

        productFilterIndexRepositoryJpa.save(entity);
    }

    @Override
    public void deleteByProductId(Long productId) {
        productFilterIndexRepositoryJpa.deleteByProductId(productId);
    }
}
