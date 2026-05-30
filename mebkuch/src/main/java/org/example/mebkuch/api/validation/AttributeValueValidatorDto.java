package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.eav.AttributeValueDto;
import org.example.mebkuch.domain.exception.ComponentException;

public class AttributeValueValidatorDto {

    public static void validate(AttributeValueDto dto) {
        if (dto == null) {
            throw new ComponentException("AttributeValue не может быть null");
        }

        boolean hasText = dto.getValueText() != null && !dto.getValueText().isBlank();
        boolean hasNumber = dto.getValueNumber() != null;
        boolean hasBoolean = dto.getValueBoolean() != null;

        if (!hasText && !hasNumber && !hasBoolean) {
            throw new ComponentException("AttributeValue должен содержать хотя бы одно значение");
        }
    }
}
