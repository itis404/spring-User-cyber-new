package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.image.ProductImageDto;
import org.example.mebkuch.domain.exception.ProductException;

public class ProductImageValidatorDto {

    public static void validate(ProductImageDto dto) {
        if (dto == null) {
            throw new ProductException("ProductImage не может быть null");
        }

        validatePath(dto.getImagePath());
    }

    public static void validatePath(String path) {
        if (path == null || path.isBlank()) {
            throw new ProductException("Путь изображения не может быть пустым");
        }
    }
}
