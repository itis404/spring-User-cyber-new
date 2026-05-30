package org.example.mebkuch.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductDto {
    private String name;

    private String description;

    private BigDecimal minPrice;

    private BigDecimal discount;


    private Long categoryId;

    private Long productTypeId;

    private Long productStatusId;

    private Long productStyleId;

    private Set<Long> sectionIds;
}
