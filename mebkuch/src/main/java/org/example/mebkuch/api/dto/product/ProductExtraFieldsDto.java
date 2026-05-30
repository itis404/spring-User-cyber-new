package org.example.mebkuch.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductExtraFieldsDto {
    private String description;
    private Long productStyleId;

    private List<Long> images;

    private List<Long> components;

    private List<Long> subProducts;

    private List<Long> attributeValues;
}
