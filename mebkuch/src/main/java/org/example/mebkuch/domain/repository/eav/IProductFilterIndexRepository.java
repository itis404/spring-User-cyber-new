package org.example.mebkuch.domain.repository.eav;

import org.springframework.stereotype.Repository;

@Repository
public interface IProductFilterIndexRepository {

    void saveIndex(Long productId,
                   Long categoryId,
                   Long attributeId,
                   Long attributeValueId);

    void deleteByProductId(Long productId);
}