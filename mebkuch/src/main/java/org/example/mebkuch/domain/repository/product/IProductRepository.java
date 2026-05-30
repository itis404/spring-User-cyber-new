package org.example.mebkuch.domain.repository.product;

import org.example.mebkuch.domain.models.filter.ProductFilter;
import org.example.mebkuch.domain.models.product.ProductExtraFieldsModel;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IProductRepository {
    Optional<ProductModel> findById(Long id);

    Optional<ProductModel> save(ProductModel productModel);

    boolean deleteById(Long id);

    Optional<ProductModel> update(Long id, ProductExtraFieldsModel productExtraFieldsModel);
    boolean existsById(Long id);

    Page<ProductModel> findAll(Pageable pageable);

    Page<ProductModel> getProductsByFilter(ProductFilter productFilter, Pageable pageable);

    Page<ProductModel> findByIds(Set<Long> ids, ProductFilter productFilter, Pageable pageable);

    boolean existsBuStyleId(Long styleId);
    boolean existsByStatusId(Long statusId);
    boolean existsByTypeId(Long typeId);
    boolean existsByCategoryId(Long categoryId);
}
