package org.example.mebkuch.api.dto.eav;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValueDto {
    private Long id;

    private Long attributeId;

    private String valueText;

    private BigDecimal valueNumber;

    private Boolean valueBoolean;
}
