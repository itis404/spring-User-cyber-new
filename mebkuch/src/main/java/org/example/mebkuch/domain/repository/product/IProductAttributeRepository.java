package org.example.mebkuch.domain.repository.product;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductAttributeRepository {

    void addAttributeToProduct(Long productId, Long attributeValueId);

    void removeAttributeFromProduct(Long productId, Long attributeValueId);

    void removeAllByProductId(Long productId);

    List<Long> findAttributeValueIdsByProductId(Long productId);
}