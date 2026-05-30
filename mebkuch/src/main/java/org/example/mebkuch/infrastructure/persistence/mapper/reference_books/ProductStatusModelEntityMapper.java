package org.example.mebkuch.infrastructure.persistence.mapper.reference_books;

import org.example.mebkuch.domain.models.reference_book.ProductStatusModel;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStatusEntity;

public class ProductStatusModelEntityMapper {

    public static ProductStatusEntity toEntity(ProductStatusModel productStatusModel){
        return ProductStatusEntity.builder()
                .name(productStatusModel.getName())
                .build();
    }

    public static ProductStatusModel toModel(ProductStatusEntity productStatusEntity) {
        return ProductStatusModel.builder()
                .id(productStatusEntity.getId())
                .name(productStatusEntity.getName())
                .build();
    }
}
