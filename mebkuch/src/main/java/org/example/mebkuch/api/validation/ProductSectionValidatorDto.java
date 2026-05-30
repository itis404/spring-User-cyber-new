package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.reference_books.ProductSectionDto;
import org.example.mebkuch.domain.exception.ProductSectionException;

public class ProductSectionValidatorDto {

    public static void validate(ProductSectionDto dto) {
        if (dto == null) {
            throw new ProductSectionException("Section не может быть null");
        }
        validateName(dto.getName());

    }

    public static void validateName(String name){
        if (name == null || name.isBlank()) {
            throw new ProductSectionException("Название секции не может быть пустым");
        }
    }
}
