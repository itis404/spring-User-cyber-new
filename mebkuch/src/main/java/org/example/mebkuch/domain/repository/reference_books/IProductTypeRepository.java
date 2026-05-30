package org.example.mebkuch.domain.repository.reference_books;

import org.example.mebkuch.domain.models.reference_book.ProductTypeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductTypeRepository {
    ProductTypeModel save(ProductTypeModel model);

    Optional<ProductTypeModel> findById(Long id);

    Optional<ProductTypeModel> findByName(String name);

    Page<ProductTypeModel> findAll(Pageable pageable);

    Optional<ProductTypeModel> updateNameById(String name, Long id);

    boolean deleteById(Long id);

    List<ProductTypeModel> findByNameContains(String nameContains);
}
