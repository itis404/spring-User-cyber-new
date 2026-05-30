package org.example.mebkuch.api.dto.component;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String material;
    private String country;
    private BigDecimal cost;
}
