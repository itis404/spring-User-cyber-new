package org.example.mebkuch.domain.repository.reference_books;

import org.example.mebkuch.domain.models.reference_book.ProductStatusModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductStatusRepository {

    ProductStatusModel save(ProductStatusModel model);

    Optional<ProductStatusModel> findById(Long id);

    Optional<ProductStatusModel> findByName(String name);

    Page<ProductStatusModel> findAll(Pageable pageable);

    Optional<ProductStatusModel> updateNameById(Long id, String name);

    boolean deleteById(Long id);

    List<ProductStatusModel> findByNameContains(String nameContains);
}
