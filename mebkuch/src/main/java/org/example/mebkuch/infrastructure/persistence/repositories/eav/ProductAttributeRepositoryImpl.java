package org.example.mebkuch.infrastructure.persistence.repositories.eav;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.repository.product.IProductAttributeRepository;
import org.example.mebkuch.infrastructure.persistence.entities.eav.ProductAttributeEntity;
import org.example.mebkuch.infrastructure.persistence.entities.eav.ProductAttributeId;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav.ProductAttributeRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class ProductAttributeRepositoryImpl implements IProductAttributeRepository {

    private final ProductAttributeRepositoryJpa repository;

    @Override
    public void addAttributeToProduct(Long productId, Long attributeValueId) {

        ProductAttributeId id = new ProductAttributeId(productId, attributeValueId);

        if (repository.existsById(id)) {
            return;
        }

        ProductAttributeEntity entity = ProductAttributeEntity.builder()
                .productId(productId)
                .attributeValueId(attributeValueId)
                .build();

        repository.save(entity);
    }

    @Override
    public void removeAttributeFromProduct(Long productId, Long attributeValueId) {
        repository.deleteByProductIdAndAttributeValueId(productId, attributeValueId);
    }

    @Override
    public void removeAllByProductId(Long productId) {
        repository.deleteByProductId(productId);
    }

    @Override
    public List<Long> findAttributeValueIdsByProductId(Long productId) {
        return repository.findByProductId(productId)
                .stream()
                .map(ProductAttributeEntity::getAttributeValueId)
                .toList();
    }
}
