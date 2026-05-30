package org.example.mebkuch.infrastructure.persistence.repositories.reference_books;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ProductStatusException;
import org.example.mebkuch.domain.models.reference_book.ProductStatusModel;
import org.example.mebkuch.domain.repository.reference_books.IProductStatusRepository;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStatusEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.reference_books.ProductStatusModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductStatusRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Primary
public class ProductStatusRepositoryImpl implements IProductStatusRepository {

    private final ProductStatusRepositoryJpa productStatusRepositoryJpa;

    @Override
    @Transactional
    public ProductStatusModel save(ProductStatusModel model) {
        ProductStatusEntity entity = productStatusRepositoryJpa.save(
                ProductStatusModelEntityMapper.toEntity(model)
        );

        return ProductStatusModelEntityMapper.toModel(entity);
    }


    @Override
    public Optional<ProductStatusModel> findById(Long id) {
        ProductStatusEntity entity = productStatusRepositoryJpa.findById(id)
                .orElseThrow(() ->
                        new ProductStatusException("Статус не найден по id: " + id)
                );

        return Optional.of(ProductStatusModelEntityMapper.toModel(entity));
    }


    @Override
    public Optional<ProductStatusModel> findByName(String name) {
        Optional<ProductStatusEntity> optional = productStatusRepositoryJpa.findByName(name);

        if (optional.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(ProductStatusModelEntityMapper.toModel(optional.get()));
    }

    @Override
    public Page<ProductStatusModel> findAll(Pageable pageable) {
        return productStatusRepositoryJpa.findAll(pageable)
                .map(ProductStatusModelEntityMapper::toModel);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return productStatusRepositoryJpa.deleteByIdReturnCount(id) > 0;
    }

    @Override
    public List<ProductStatusModel> findByNameContains(String nameContains) {
        return productStatusRepositoryJpa.findByNameContains(nameContains)
                .stream()
                .map(ProductStatusModelEntityMapper::toModel)
                .toList();
    }


    @Override
    @Transactional
    public Optional<ProductStatusModel> updateNameById(Long id, String name) {
        ProductStatusEntity entity = productStatusRepositoryJpa.findById(id)
                .orElseThrow(() ->
                        new ProductStatusException("Статус не найден по id: " + id)
                );

        entity.setName(name);

        return Optional.of(ProductStatusModelEntityMapper.toModel(entity));
    }
}