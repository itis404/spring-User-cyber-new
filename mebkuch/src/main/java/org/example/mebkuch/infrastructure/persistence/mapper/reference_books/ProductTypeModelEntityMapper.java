package org.example.mebkuch.infrastructure.persistence.mapper.reference_books;

import org.example.mebkuch.domain.models.reference_book.ProductTypeModel;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductTypeEntity;

public class ProductTypeModelEntityMapper {

    public static ProductTypeEntity toEntity(ProductTypeModel productTypeModel){
        return ProductTypeEntity.builder()
                .name(productTypeModel.getName())
                .hasComponents(productTypeModel.getHasComponents())
                .build();
    }

    public static ProductTypeModel toModel(ProductTypeEntity productTypeEntity){
        return ProductTypeModel.builder()
                .id(productTypeEntity.getId())
                .name(productTypeEntity.getName())
                .hasComponents(productTypeEntity.getHasComponents())
                .build();
    }

}