package org.example.mebkuch.infrastructure.persistence.entities.eav;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_attribute_value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ProductAttributeId.class)
public class ProductAttributeEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Id
    @Column(name = "attribute_value_id")
    private Long attributeValueId;
}
