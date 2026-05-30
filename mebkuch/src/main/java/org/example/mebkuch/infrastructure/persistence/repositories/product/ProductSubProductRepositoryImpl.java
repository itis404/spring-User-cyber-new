package org.example.mebkuch.infrastructure.persistence.repositories.product;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.repository.product.IProductSubProductRepository;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductSubProductEntity;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductSubProductId;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductSubProductRepositoryJpa;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductSubProductRepositoryImpl implements IProductSubProductRepository {

    private final ProductSubProductRepositoryJpa jpa;
    private final ProductRepositoryJpa productRepository;

    @Override
    public void add(Long productId, Long subProductId) {

        ProductSubProductEntity entity = ProductSubProductEntity.builder()
                .id(new ProductSubProductId(productId, subProductId))
                .product(productRepository.findById(productId).orElseThrow(() -> new ProductException("продукта по такому id нет")))
                .subProduct(productRepository.findById(subProductId).orElseThrow(() -> new ProductException("продукта по такому id нет")))
                .build();

        jpa.save(entity);
    }

    @Override
    @Transactional
    public void remove(Long productId, Long subProductId) {
        jpa.deleteByProduct_IdAndSubProduct_Id(productId, subProductId);
    }

    @Override
    public List<Long> findSubProducts(Long productId) {
        return jpa.findByProduct_Id(productId)
                .stream()
                .map(e -> e.getSubProduct().getId())
                .toList();
    }
}