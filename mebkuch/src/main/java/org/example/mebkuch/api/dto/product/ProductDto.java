package org.example.mebkuch.api.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDto {
    private Long id;

    private String name;

    private String description;

    private BigDecimal minPrice;

    private BigDecimal discount;

    private LocalDate createdAt;

    // связи
    private Long categoryId;

    private Long productTypeId;

    private Long productStatusId;

    private Long productStyleId;

    @Builder.Default
    private Set<Long> sectionIds = new HashSet<>();

    @Builder.Default
    private List<Long> images = new ArrayList<>();

    @Builder.Default
    private List<Long> components = new ArrayList<>();

    @Builder.Default
    private List<Long> subProducts = new ArrayList<>();

    @Builder.Default
    private List<Long> attributeValues = new ArrayList<>();
}
