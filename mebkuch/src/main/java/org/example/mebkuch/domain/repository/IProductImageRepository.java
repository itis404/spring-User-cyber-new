package org.example.mebkuch.domain.repository;

import org.example.mebkuch.domain.models.image.ProductImageModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductImageRepository {

    Optional<ProductImageModel> findById(Long id);

    List<ProductImageModel> findByProductId(Long productId);

    ProductImageModel save(ProductImageModel image);

    ProductImageModel update(Long id, String imagePath, Boolean isMain, Integer sortOrder);

    boolean deleteById(Long id);

    boolean existsById(Long id);
}