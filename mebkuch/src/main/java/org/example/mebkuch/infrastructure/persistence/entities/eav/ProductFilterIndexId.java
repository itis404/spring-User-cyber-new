package org.example.mebkuch.infrastructure.persistence.entities.eav;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterIndexId implements Serializable {
    private Long productId;
    private Long attributeValueId;
}