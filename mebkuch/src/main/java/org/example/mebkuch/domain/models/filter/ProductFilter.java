package org.example.mebkuch.domain.models.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductFilter {

    private String name;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private BigDecimal minDiscount;
    private BigDecimal maxDiscount;

    private LocalDate createdAt;

    private ComponentFilter componentFilter;

    // связи
    @JsonProperty("categoryId")
    private List<Long> categoryId;

    @JsonProperty("productTypeId")
    private List<Long> productTypeId;

    @JsonProperty("productStatusId")
    private List<Long> productStatusId;

    @JsonProperty("productStyleId")
    private List<Long> productStyleId;

    // EAV фильтрация
    private List<AttributeFilter> attributes;
}