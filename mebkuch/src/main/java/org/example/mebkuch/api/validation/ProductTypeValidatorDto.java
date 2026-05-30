package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.reference_books.ProductTypeDto;
import org.example.mebkuch.domain.exception.ProductTypeException;

public class ProductTypeValidatorDto {

    public static void validate(ProductTypeDto dto) {
        if (dto == null) {
            throw new ProductTypeException("Type не может быть null");
        }

        validateName(dto.getName());

        if (dto.getHasComponents() == null) {
            throw new ProductTypeException("hasComponents не может быть null");
        }
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ProductTypeException("Название не может быть null или пустым");
        }
    }
}