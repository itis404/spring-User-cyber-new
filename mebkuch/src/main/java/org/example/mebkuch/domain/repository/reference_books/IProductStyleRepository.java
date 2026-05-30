package org.example.mebkuch.domain.repository.reference_books;

import org.example.mebkuch.domain.models.reference_book.ProductStyleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductStyleRepository {
    ProductStyleModel save(ProductStyleModel model);

    Optional<ProductStyleModel> findById(Long id);

    Optional<ProductStyleModel> findByName(String name);

    Page<ProductStyleModel> findAll(Pageable pageable);

    Optional<ProductStyleModel> updateNameById(String name, Long id);

    boolean deleteById(Long id);

    List<ProductStyleModel> getByNameContains(String nameContains);
}
