package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.product.CreateProductDto;
import org.example.mebkuch.api.dto.product.ProductDto;
import org.example.mebkuch.domain.exception.ProductException;

import java.math.BigDecimal;
import java.util.Collection;

public class ProductValidatorDto {

    public static void validate(ProductDto dto) {
        if (dto == null) {
            throw new ProductException("Product не может быть null");
        }

        validateName(dto.getName());
        validateDescription(dto.getDescription());
        validatePrice(dto.getMinPrice());
        validateDiscount(dto.getDiscount());

        validateIds(dto.getCategoryId(), "categoryId");
        validateIds(dto.getProductTypeId(), "productTypeId");
        validateIds(dto.getProductStatusId(), "productStatusId");
        validateIds(dto.getProductStyleId(), "productStyleId");
    }

    public static void validateForeCreate(CreateProductDto dto) {
        if (dto == null) {
            throw new ProductException("Product не может быть null");
        }

        validateName(dto.getName());
        validateDescription(dto.getDescription());
        validatePrice(dto.getMinPrice());
        validateDiscount(dto.getDiscount());

        validateIds(dto.getCategoryId(), "categoryId");
        validateIds(dto.getProductTypeId(), "productTypeId");
        validateIds(dto.getProductStatusId(), "productStatusId");
        validateIds(dto.getProductStyleId(), "productStyleId");
    }



    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ProductException("Название продукта не может быть пустым");
        }
    }

    public static void validateDescription(String description) {
        if (description != null && description.length() > 255) {
            throw new ProductException("Описание слишком длинное");
        }
    }

    public static void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new ProductException("Цена не может быть null");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductException("Цена не может быть отрицательной");
        }
    }

    public static void validateDiscount(BigDecimal discount) {
        if (discount == null) return;

        if (discount.compareTo(BigDecimal.ZERO) < 0 ||
                discount.compareTo(BigDecimal.valueOf(80)) > 0) {
            throw new ProductException("Скидка должна быть от 0 до 10");
        }
    }


    public static void validateIds(Long id, String field) {
        if (id == null) {
            throw new ProductException(field + " не может быть null");
        }
    }

    public static void validateCollection(Collection<?> list, String field) {
        if (list != null && list.isEmpty()) {
            throw new ProductException(field + " не может быть пустым списком");
        }
    }
}
