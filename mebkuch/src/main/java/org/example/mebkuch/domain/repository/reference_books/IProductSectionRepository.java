package org.example.mebkuch.domain.repository.reference_books;


import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.models.reference_book.ProductSectionModel;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductSectionRepository {

    ProductSectionModel save(ProductSectionModel productSectionModel);

    Optional<ProductSectionModel> findById(Long id);

    Optional<ProductSectionModel> findByName(String name);

    Page<ProductSectionModel> findAll(Pageable pageable);

    boolean deleteById(Long id);

    Optional<ProductSectionModel> updateNameById(Long id, String name);

    @Transactional
    void addProductToSection(Long sectionId, Long productId);

    @Transactional
    void removeProductFromSection(Long sectionId, Long productId);

    Page<ProductModel> getProductsBySection(Long sectionId, Pageable pageable);

    List<ProductSectionModel> findByNameContains(String nameContains);
}