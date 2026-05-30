package org.example.mebkuch.infrastructure.persistence.mapper.reference_books;

import org.example.mebkuch.domain.models.reference_book.ProductSectionModel;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductSectionEntity;

public class ProductSectionModelEntityMapper {

    public static ProductSectionEntity toEntity(ProductSectionModel productSectionModel){
        return ProductSectionEntity.builder()
                .name(productSectionModel.getName())
                .imageUrl(productSectionModel.getImageUrl())
                .build();
    }

    public static ProductSectionModel toModel(ProductSectionEntity productSectionEntity){
        return ProductSectionModel.builder()
                .id(productSectionEntity.getId())
                .name(productSectionEntity.getName())
                .imageUrl(productSectionEntity.getImageUrl())
                .build();
    }
}
