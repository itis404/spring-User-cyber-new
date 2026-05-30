package org.example.mebkuch.domain.models.filter;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeFilter {

    private Long attributeId;

    private String valueText;
    private Boolean valueBoolean;

    private BigDecimal minValue;
    private BigDecimal maxValue;
}
