package org.example.mebkuch.infrastructure.persistence.repositories.reference_books;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.exception.ProductSectionException;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.models.reference_book.ProductSectionModel;
import org.example.mebkuch.domain.repository.reference_books.IProductSectionRepository;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductSectionEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.product.ProductModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.mapper.reference_books.ProductSectionModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductSectionRepositoryJpa;
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
public class ProductSectionRepositoryImpl implements IProductSectionRepository {

    private final ProductSectionRepositoryJpa productSectionRepositoryJpa;
    private final ProductRepositoryJpa productRepositoryJpa;

    @Override
    @Transactional
    public ProductSectionModel save(ProductSectionModel model) {

        ProductSectionEntity entity = ProductSectionModelEntityMapper.toEntity(model);

        ProductSectionEntity saved = productSectionRepositoryJpa.save(entity);

        return ProductSectionModelEntityMapper.toModel(saved);
    }

    @Override
    public Optional<ProductSectionModel> findById(Long id) {
        return productSectionRepositoryJpa.findById(id)
                .map(ProductSectionModelEntityMapper::toModel);
    }

    @Override
    public Optional<ProductSectionModel> findByName(String name) {
        return productSectionRepositoryJpa.findByName(name)
                .map(ProductSectionModelEntityMapper::toModel);
    }

    @Override
    public Page<ProductSectionModel> findAll(Pageable pageable) {
        return productSectionRepositoryJpa.findAll(pageable)
                .map(ProductSectionModelEntityMapper::toModel);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (!productSectionRepositoryJpa.existsById(id)) {
            return false;
        }

        productSectionRepositoryJpa.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public Optional<ProductSectionModel> updateNameById(Long id, String name) {

        ProductSectionEntity entity = productSectionRepositoryJpa.findById(id)
                .orElseThrow(() -> new ProductSectionException("Section not found"));

        entity.setName(name);

        return Optional.of(ProductSectionModelEntityMapper.toModel(entity));
    }

    @Transactional
    @Override
    public void addProductToSection(Long sectionId, Long productId) {

        ProductEntity product = productRepositoryJpa.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found"));

        ProductSectionEntity section = productSectionRepositoryJpa.findById(sectionId)
                .orElseThrow(() -> new ProductSectionException("Section not found"));

        product.getSections().add(section);
    }


    @Transactional
    @Override
    public void removeProductFromSection(Long sectionId, Long productId) {

        ProductEntity product = productRepositoryJpa.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found"));

        ProductSectionEntity section = productSectionRepositoryJpa.findById(sectionId)
                .orElseThrow(() -> new ProductSectionException("Section not found"));

        product.getSections().remove(section);
    }

    @Override
    public Page<ProductModel> getProductsBySection(Long sectionId, Pageable pageable) {

        if (!productSectionRepositoryJpa.existsById(sectionId)) {
            throw new ProductSectionException("Section not found");
        }

        return productRepositoryJpa.findAllBySectionId(sectionId, pageable).map(ProductModelEntityMapper::toModel);
    }

    @Override
    public List<ProductSectionModel> findByNameContains(String nameContains) {
        List<ProductSectionEntity> productSectionEntityList = productSectionRepositoryJpa.findByNameContains(nameContains);

        return productSectionEntityList.stream().map(ProductSectionModelEntityMapper::toModel).toList();
    }
}