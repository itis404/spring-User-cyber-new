package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.component.ComponentCategoryDto;
import org.example.mebkuch.domain.exception.ComponentException;

public class ComponentCategoryValidatorDto {

    public static void validate(ComponentCategoryDto dto) {
        if (dto == null) {
            throw new ComponentException("ComponentCategory не может быть null");
        }

        validateName(dto.getName());
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ComponentException("Название категории компонента не может быть пустым");
        }
    }
}