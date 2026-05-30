package org.example.mebkuch.infrastructure.persistence.repositories;

import lombok.RequiredArgsConstructor;

import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.models.image.ProductImageModel;
import org.example.mebkuch.domain.repository.IProductImageRepository;
import org.example.mebkuch.infrastructure.persistence.entities.image.ProductImageEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.image.ProductImageModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.ProductImageRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class ProductImageRepositoryImpl implements IProductImageRepository {

    private final ProductImageRepositoryJpa imageRepositoryJpa;
    private final ProductRepositoryJpa productRepositoryJpa;

    @Override
    public Optional<ProductImageModel> findById(Long id) {
        return imageRepositoryJpa.findById(id)
                .map(ProductImageModelEntityMapper::toModel);
    }

    @Override
    public List<ProductImageModel> findByProductId(Long productId) {
        return imageRepositoryJpa.findByProductId(productId)
                .stream()
                .map(ProductImageModelEntityMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public ProductImageModel save(ProductImageModel image) {
        ProductImageEntity entity = ProductImageModelEntityMapper.toEntity(image, productRepositoryJpa);

        return ProductImageModelEntityMapper.toModel(imageRepositoryJpa.save(entity));
    }

    @Override
    @Transactional
    public ProductImageModel update(Long id, String imagePath, Boolean isMain, Integer sortOrder) {
        ProductImageEntity productImageEntity = imageRepositoryJpa.findById(id)
                .orElseThrow(() -> new ProductException("такого изображения не существует"));

        productImageEntity.setImagePath(imagePath);
        productImageEntity.setIsMain(isMain);
        productImageEntity.setSortOrder(sortOrder);

        return ProductImageModelEntityMapper.toModel(productImageEntity);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!imageRepositoryJpa.existsById(id)) {
            return false;
        }
        imageRepositoryJpa.deleteById(id);
        return true;
    }

    @Override
    public boolean existsById(Long id) {
        return imageRepositoryJpa.existsById(id);
    }
}