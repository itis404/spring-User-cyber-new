package org.example.mebkuch.domain.repository.product;

import java.util.List;

public interface IProductSubProductRepository {

    void add(Long productId, Long subProductId);

    void remove(Long productId, Long subProductId);

    List<Long> findSubProducts(Long productId);
}
