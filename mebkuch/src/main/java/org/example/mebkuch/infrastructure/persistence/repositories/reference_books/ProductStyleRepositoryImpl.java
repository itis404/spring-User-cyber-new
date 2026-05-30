package org.example.mebkuch.infrastructure.persistence.repositories.reference_books;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ProductStyleException;
import org.example.mebkuch.domain.models.reference_book.ProductStatusModel;
import org.example.mebkuch.domain.models.reference_book.ProductStyleModel;
import org.example.mebkuch.domain.repository.reference_books.IProductStyleRepository;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStyleEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.reference_books.ProductStyleModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductStyleRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class ProductStyleRepositoryImpl implements IProductStyleRepository {

    private final ProductStyleRepositoryJpa productStyleRepositoryJpa;


    @Override
    @Transactional
    public ProductStyleModel save(ProductStyleModel model) {
        ProductStyleEntity entity = productStyleRepositoryJpa.save(
                ProductStyleModelEntityMapper.toEntity(model)
        );

        return ProductStyleModelEntityMapper.toModel(entity);
    }

    @Override
    public Optional<ProductStyleModel> findById(Long id) {
        ProductStyleEntity entity = productStyleRepositoryJpa.findById(id)
                .orElseThrow(() ->
                        new ProductStyleException("Style not found with id: " + id)
                );

        return Optional.of(ProductStyleModelEntityMapper.toModel(entity));
    }


    @Override
    public Optional<ProductStyleModel> findByName(String name) {
        Optional<ProductStyleEntity> productStyleEntityOptional = productStyleRepositoryJpa.findByName(name);

        if (productStyleEntityOptional.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(ProductStyleModelEntityMapper.toModel(productStyleEntityOptional.get()));

    }

    @Override
    public Page<ProductStyleModel> findAll(Pageable pageable) {
        return productStyleRepositoryJpa.findAll(pageable)
                .map(ProductStyleModelEntityMapper::toModel);
    }

    @Override
    @Transactional
    public Optional<ProductStyleModel> updateNameById(String name, Long id) {
        ProductStyleEntity productStyleEntity = productStyleRepositoryJpa.findById(id)
                .orElseThrow(() -> new ProductStyleException("данный статус по заданному id не найден"));

        productStyleEntity.setName(name);

        return Optional.of(ProductStyleModelEntityMapper.toModel(productStyleEntity));
    }


    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (!productStyleRepositoryJpa.existsById(id)) {
            throw new ProductStyleException("Style not found with id: " + id);
        }

        return productStyleRepositoryJpa.deleteByIdReturnCount(id) > 0;
    }

    @Override
    public List<ProductStyleModel> getByNameContains(String nameContains) {
        return productStyleRepositoryJpa.findByNameContains(nameContains)
                .stream()
                .map(ProductStyleModelEntityMapper::toModel)
                .toList();
    }
}