package org.example.mebkuch.infrastructure.persistence.mapper.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.CategoryException;
import org.example.mebkuch.domain.exception.ProductSectionException;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryEntity;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductSectionEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStatusEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStyleEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductTypeEntity;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.category.CategoryRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductSectionRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductStatusRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductStyleRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductTypeRepositoryJpa;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductModelEntityMapper {

    public static ProductModel toModel(ProductEntity entity) {

        log.info("id компонентов: " + entity.getProductComponentEntities().stream().map(pce -> pce.getComponent().getId()).toList());

        return ProductModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .minPrice(entity.getMinPrice())
                .discount(entity.getDiscount())
                .createdAt(entity.getCreatedAt())

                .categoryId(entity.getСategoryEntity().getId())
                .productTypeId(entity.getProductTypeEntity().getId())
                .productStatusId(entity.getProductStatusEntity().getId())
                .productStyleId(entity.getProductStyleEntity() != null
                        ? entity.getProductStyleEntity().getId()
                        : null)

                .images(entity.getImages().stream()
                        .map(image -> image.getId())
                        .toList())

                .components(entity.getProductComponentEntities().stream()
                        .map(pce -> pce.getComponent().getId()).toList())

                .atributeValues(entity.getAttributeValues().stream()
                        .map(attributeValueEntity -> attributeValueEntity.getId())
                        .toList())
                .build();
    }

    public static ProductEntity toEntity(ProductModel model) {
        return ProductEntity.builder()
                .name(model.getName())
                .description(model.getDescription())
                .minPrice(model.getMinPrice())
                .discount(model.getDiscount())
                .createdAt(model.getCreatedAt())
                .build();
    }

    public static ProductEntity toEntityForCreate(
            ProductModel model,
            CategoryRepositoryJpa categoryRepo,
            ProductTypeRepositoryJpa typeRepo,
            ProductStatusRepositoryJpa statusRepo,
            ProductStyleRepositoryJpa styleRepo,
            ProductSectionRepositoryJpa sectionRepo
    ) {


        CategoryEntity category = categoryRepo.findById(model.getCategoryId())
                .orElseThrow(() -> new CategoryException("Category not found: " + model.getCategoryId()));

        ProductTypeEntity type = typeRepo.findById(model.getProductTypeId())
                .orElseThrow(() -> new CategoryException("Type not found: " + model.getProductTypeId()));

        ProductStatusEntity status = statusRepo.findById(model.getProductStatusId())
                .orElseThrow(() -> new CategoryException("Status not found: " + model.getProductStatusId()));

        ProductStyleEntity style = null;
        if (model.getProductStyleId() != null) {
            style = styleRepo.findById(model.getProductStyleId())
                    .orElseThrow(() -> new CategoryException("Style not found: " + model.getProductStyleId()));
        }

        List<ProductSectionEntity> foundSections = sectionRepo.findAllById(model.getSectionIds());

        if (foundSections.size() != model.getSectionIds().size()) {
            throw new ProductSectionException("Some sections not found");
        }

        Set<ProductSectionEntity> sections = new HashSet<>(foundSections);


        return ProductEntity.builder()
                .name(model.getName())
                .description(model.getDescription())
                .minPrice(model.getMinPrice())
                .discount(model.getDiscount())
                .сategoryEntity(category)
                .productTypeEntity(type)
                .productStatusEntity(status)
                .productStyleEntity(style)
                .sections(sections)
                .build();
    }
}