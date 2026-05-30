package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.eav.AttributeDto;
import org.example.mebkuch.domain.exception.ComponentException;
import org.example.mebkuch.domain.models.eav.AttributeType;

public class AttributeValidatorDto {

    public static void validate(AttributeDto dto) {
        if (dto == null) {
            throw new ComponentException("Attribute не может быть null");
        }

        validateName(dto.getName());
        validateType(dto.getType());
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ComponentException("Имя атрибута не может быть пустым");
        }
    }

    public static void validateType(AttributeType type) {
        if (type == null) {
            throw new ComponentException("Тип атрибута не может быть null");
        }
    }
}
