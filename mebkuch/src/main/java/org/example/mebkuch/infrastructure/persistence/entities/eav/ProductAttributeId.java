package org.example.mebkuch.infrastructure.persistence.entities.eav;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductAttributeId implements Serializable {

    private Long productId;
    private Long attributeValueId;
}