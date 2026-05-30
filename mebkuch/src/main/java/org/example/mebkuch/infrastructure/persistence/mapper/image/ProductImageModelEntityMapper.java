package org.example.mebkuch.infrastructure.persistence.mapper.image;

import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.models.image.ProductImageModel;
import org.example.mebkuch.infrastructure.persistence.entities.image.ProductImageEntity;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductRepositoryJpa;

public class ProductImageModelEntityMapper {

    public static ProductImageModel toModel(ProductImageEntity entity) {
        if (entity == null) return null;

        return ProductImageModel.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .imagePath(entity.getImagePath())
                .isMain(entity.getIsMain())
                .sortOrder(entity.getSortOrder())
                .build();
    }

    public static ProductImageEntity toEntity(ProductImageModel image, ProductRepositoryJpa productRepositoryJpa) {

        ProductEntity product = productRepositoryJpa.findById(image.getProductId())
                .orElseThrow(()-> new ProductException("такого продукта не существует"));

        return ProductImageEntity.builder()
                .product(product)
                .imagePath(image.getImagePath())
                .isMain(image.getIsMain())
                .sortOrder(image.getSortOrder())
                .build();
    }

}
