package org.example.mebkuch.domain.models.filter;

import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ComponentFilter {
    private String name;
    private String material;
    private String country;
    private BigDecimal beginCost;
    private BigDecimal endCost;
}