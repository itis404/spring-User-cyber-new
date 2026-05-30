package org.example.mebkuch.domain.models.eav;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ProductTypeException;

import java.math.BigDecimal;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeValueModel {

    @EqualsAndHashCode.Include
    private Long id;

    private Long attributeId;

    private String valueText;

    private BigDecimal valueNumber;

    private Boolean valueBoolean;


    public Object getValue() {
        if (valueText != null) return valueText;
        if (valueNumber != null) return valueNumber;
        if (valueBoolean != null) return valueBoolean;
        return null;
    }


    public void validate() {
        int count = 0;

        if (valueText != null) count++;
        if (valueNumber != null) count++;
        if (valueBoolean != null) count++;

        if (count != 1) {
            log.error("Invalid attribute value: {}", this);
            throw new ProductTypeException("Exactly one value must be set");
        }
    }

}