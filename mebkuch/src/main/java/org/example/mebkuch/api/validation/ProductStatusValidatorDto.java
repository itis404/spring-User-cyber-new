package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.reference_books.ProductStatusDto;
import org.example.mebkuch.domain.exception.ProductStatusException;

public class ProductStatusValidatorDto {

    public static void validate(ProductStatusDto dto) {
        if (dto == null) {
            throw new ProductStatusException("Status не может быть null");
        }
        validateName(dto.getName());
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ProductStatusException("Название статуса не может быть null или пустым");
        }
    }
}
