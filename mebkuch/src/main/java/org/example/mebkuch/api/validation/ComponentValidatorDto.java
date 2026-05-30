package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.component.ComponentDto;
import org.example.mebkuch.domain.exception.ComponentException;

import java.math.BigDecimal;

public class ComponentValidatorDto {

    public static void validate(ComponentDto dto) {
        if (dto == null) {
            throw new ComponentException("Component не может быть null");
        }

        validateName(dto.getName());
        validateMaterial(dto.getMaterial());
        validateCountry(dto.getCountry());
        validateCost(dto.getCost());
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ComponentException("Название компонента не может быть пустым");
        }
    }

    public static void validateMaterial(String material) {
        if (material == null || material.isBlank()) {
            throw new ComponentException("Материал не может быть пустым");
        }
    }

    public static void validateCountry(String country) {
        if (country == null || country.isBlank()) {
            throw new ComponentException("Страна не может быть пустой");
        }
    }

    public static void validateCost(BigDecimal cost) {
        if (cost == null) {
            throw new ComponentException("Стоимость не может быть null");
        }

        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new ComponentException("Стоимость не может быть отрицательной");
        }
    }
}
