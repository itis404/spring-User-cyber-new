package org.example.mebkuch.api.mapper;

import org.example.mebkuch.api.dto.product.CreateProductDto;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.domain.models.product.ProductModel;

public class ProductDtoModelMapper {

    public static ProductDto toDto(ProductModel productModel){
        return ProductDto.builder()
                .id(productModel.getId())
                .name(productModel.getName())
                .description(productModel.getDescription())
                .minPrice(productModel.getMinPrice())
                .discount(productModel.getDiscount())
                .createdAt(productModel.getCreatedAt())
                .categoryId(productModel.getCategoryId())
                .productTypeId(productModel.getProductTypeId())
                .productStatusId(productModel.getProductStatusId())
                .productStyleId(productModel.getProductStyleId())
                .sectionIds(productModel.getSectionIds())
                .images(productModel.getImages())
                .components(productModel.getComponents())
                .subProducts(productModel.getSubProducts())
                .attributeValues(productModel.getAtributeValues())
                .build();
    }

    public static ProductModel toModel(ProductDto dto) {

        if (dto == null) {
            return null;
        }

        return ProductModel.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .minPrice(dto.getMinPrice())
                .discount(dto.getDiscount())
                .createdAt(dto.getCreatedAt()) 
                .categoryId(dto.getCategoryId())
                .productTypeId(dto.getProductTypeId())
                .productStatusId(dto.getProductStatusId())
                .productStyleId(dto.getProductStyleId())
                .sectionIds(dto.getSectionIds())
                .images(dto.getImages())
                .components(dto.getComponents())
                .subProducts(dto.getSubProducts())
                .build();
    }

    public static ProductModel toModelForCreate(CreateProductDto dto) {
        return ProductModel.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .minPrice(dto.getMinPrice())
                .discount(dto.getDiscount())
                .categoryId(dto.getCategoryId())
                .productTypeId(dto.getProductTypeId())
                .productStatusId(dto.getProductStatusId())
                .productStyleId(dto.getProductStyleId())
                .sectionIds(dto.getSectionIds())
                .build();
    }

}
