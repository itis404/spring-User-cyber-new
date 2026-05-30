package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.product.ProductExtraFieldsDto;
import org.example.mebkuch.domain.exception.ProductException;

public class ProductExtraFieldsValidatorDto {

    public static void validate(ProductExtraFieldsDto dto) {
        if (dto == null) {
            throw new ProductException("ExtraFields не может быть null");
        }

        if (dto.getProductStyleId() == null) {
            throw new ProductException("productStyleId обязателен");
        }
    }
}