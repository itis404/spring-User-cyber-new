package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.reference_books.ProductStyleDto;
import org.example.mebkuch.domain.exception.ProductStyleException;

public class ProductStyleValidatorDto {

    public static void validate(ProductStyleDto dto) {
        if (dto == null) {
            throw new ProductStyleException("Style не может быть null");
        }

        validateName(dto.getName());
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ProductStyleException("Название стиля не может быть пустым");
        }
    }
}