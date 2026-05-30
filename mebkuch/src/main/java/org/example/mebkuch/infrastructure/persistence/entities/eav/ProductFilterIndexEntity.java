package org.example.mebkuch.infrastructure.persistence.entities.eav;

import jakarta.persistence.*;
import lombok.*;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;

@Entity
@Table(name = "product_filter_index")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ProductFilterIndexId.class)
public class ProductFilterIndexEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Id
    @Column(name = "attribute_value_id")
    private Long attributeValueId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "attribute_id", nullable = false)
    private Long attributeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_value_id", insertable = false, updatable = false)
    private AttributeValueEntity attributeValue;
}
