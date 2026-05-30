package org.example.mebkuch.infrastructure.persistence.mapper.reference_books;

import org.example.mebkuch.domain.models.reference_book.ProductStyleModel;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStyleEntity;

public class ProductStyleModelEntityMapper {

    public static ProductStyleEntity toEntity(ProductStyleModel productStyleModel){
        return ProductStyleEntity.builder()
                .name(productStyleModel.getName())
                .imageUrl(productStyleModel.getImageUrl())
                .build();
    }

    public static ProductStyleModel toModel(ProductStyleEntity productStyleEntity){
        return ProductStyleModel.builder()
                .id(productStyleEntity.getId())
                .name(productStyleEntity.getName())
                .imageUrl(productStyleEntity.getImageUrl())
                .build();
    }

}
