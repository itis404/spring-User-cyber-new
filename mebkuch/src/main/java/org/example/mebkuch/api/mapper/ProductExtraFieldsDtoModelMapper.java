package org.example.mebkuch.api.mapper;

import org.example.mebkuch.api.dto.product.ProductExtraFieldsDto;
import org.example.mebkuch.domain.models.product.ProductExtraFieldsModel;

public class ProductExtraFieldsDtoModelMapper {

    public static ProductExtraFieldsModel toModel(ProductExtraFieldsDto productExtraFieldsDto){
        return ProductExtraFieldsModel.builder()
                .description(productExtraFieldsDto.getDescription())
                .productStyleId(productExtraFieldsDto.getProductStyleId())
                .images(productExtraFieldsDto.getImages())
                .components(productExtraFieldsDto.getComponents())
                .subProducts(productExtraFieldsDto.getSubProducts())
                .attributeValues(productExtraFieldsDto.getAttributeValues())
                .build();
    }

    public static ProductExtraFieldsDto toDto(ProductExtraFieldsModel productExtraFieldsModel){
        return ProductExtraFieldsDto.builder()
                .description(productExtraFieldsModel.getDescription())
                .productStyleId(productExtraFieldsModel.getProductStyleId())
                .images(productExtraFieldsModel.getImages())
                .components(productExtraFieldsModel.getComponents())
                .subProducts(productExtraFieldsModel.getSubProducts())
                .attributeValues(productExtraFieldsModel.getAttributeValues())
                .build();
    }

}
