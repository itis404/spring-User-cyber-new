package org.example.mebkuch.infrastructure.persistence.repositories.reference_books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ProductTypeException;
import org.example.mebkuch.domain.models.reference_book.ProductTypeModel;
import org.example.mebkuch.domain.repository.reference_books.IProductTypeRepository;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductTypeEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.reference_books.ProductTypeModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductTypeRepositoryJpa;
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
@Slf4j
public class ProductTypeRepositoryImpl implements IProductTypeRepository {

    private final ProductTypeRepositoryJpa productTypeRepositoryJpa;

    @Override
    @Transactional
    public ProductTypeModel save(ProductTypeModel model) {
        ProductTypeEntity entity = productTypeRepositoryJpa.save(
                ProductTypeModelEntityMapper.toEntity(model)
        );

        return ProductTypeModelEntityMapper.toModel(entity);
    }


    @Override
    public Optional<ProductTypeModel> findById(Long id) {
        ProductTypeEntity entity = productTypeRepositoryJpa.findById(id)
                .orElseThrow(() ->
                        new ProductTypeException("Type not found with id: " + id)
                );

        return Optional.of(ProductTypeModelEntityMapper.toModel(entity));
    }


    @Override
    public Optional<ProductTypeModel> findByName(String name) {
        Optional<ProductTypeEntity> optional = productTypeRepositoryJpa.findByName(name);

        if (optional.isEmpty()) {
            log.warn("findByName: Type not found with name: {}", name);
            return Optional.empty();
        }

        return Optional.of(ProductTypeModelEntityMapper.toModel(optional.get()));
    }

    @Override
    public Page<ProductTypeModel> findAll(Pageable pageable) {
        return productTypeRepositoryJpa.findAll(pageable)
                .map(ProductTypeModelEntityMapper::toModel);
    }


    @Transactional
    @Override
    public Optional<ProductTypeModel> updateNameById(String name, Long id) {
        ProductTypeEntity entity = productTypeRepositoryJpa.findById(id)
                .orElseThrow(() ->
                        new ProductTypeException("Type not found with id: " + id)
                );

        entity.setName(name);

        return Optional.of(ProductTypeModelEntityMapper.toModel(entity));
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (!productTypeRepositoryJpa.existsById(id)) {
            log.error("deleteById: Type not found with id: {}", id);
            throw new ProductTypeException("Type not found with id: " + id);
        }

        return productTypeRepositoryJpa.deleteByIdReturnCount(id) > 0;
    }

    @Override
    public List<ProductTypeModel> findByNameContains(String nameContains) {
        List<ProductTypeModel> list = productTypeRepositoryJpa.findByNameContains(nameContains)
                .stream()
                .map(ProductTypeModelEntityMapper::toModel)
                .toList();

        log.info("findByNameContains: nameContains={}, found={}", nameContains, list.size());

        return list;
    }
}