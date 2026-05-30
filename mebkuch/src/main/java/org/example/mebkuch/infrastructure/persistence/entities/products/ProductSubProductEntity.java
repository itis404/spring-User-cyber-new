package org.example.mebkuch.infrastructure.persistence.entities.products;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_sub_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSubProductEntity {

    @EmbeddedId
    private ProductSubProductId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "id_product")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subProductId")
    @JoinColumn(name = "id_sub_product")
    private ProductEntity subProduct;
}