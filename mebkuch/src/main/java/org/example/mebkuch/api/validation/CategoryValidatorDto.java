package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.category.CategoryDto;
import org.example.mebkuch.domain.exception.CategoryException;

public class CategoryValidatorDto {

    public static void validate(CategoryDto dto) {
        if (dto == null) {
            throw new CategoryException("Category не может быть null");
        }

        validateName(dto.getName());
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new CategoryException("Название категории не может быть пустым");
        }

        if (name.length() > 150) {
            throw new CategoryException("Название категории слишком длинное");
        }
    }
}
