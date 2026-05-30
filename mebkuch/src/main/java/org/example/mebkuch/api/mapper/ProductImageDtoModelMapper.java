package org.example.mebkuch.api.mapper;

import org.example.mebkuch.api.dto.image.ProductImageDto;
import org.example.mebkuch.domain.models.image.ProductImageModel;

public class ProductImageDtoModelMapper {

    public static ProductImageDto toDto(ProductImageModel model) {
        if (model == null) return null;

        return ProductImageDto.builder()
                .id(model.getId())
                .productId(model.getProductId())
                .imagePath(model.getImagePath())
                .isMain(model.getIsMain())
                .sortOrder(model.getSortOrder())
                .build();
    }

    public static ProductImageModel toModel(ProductImageDto dto) {
        if (dto == null) return null;

        return ProductImageModel.builder()
                .productId(dto.getProductId())
                .imagePath(dto.getImagePath())
                .isMain(dto.getIsMain())
                .sortOrder(dto.getSortOrder())
                .build();
    }
}