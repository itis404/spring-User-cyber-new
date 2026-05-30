package org.example.mebkuch.domain.models.component;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentModel {
    private Long id;
    private String name;
    private Long categoryId;
    private String material;
    private String country;
    private BigDecimal cost;
}