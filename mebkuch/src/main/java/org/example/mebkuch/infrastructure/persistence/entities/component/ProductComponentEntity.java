package org.example.mebkuch.infrastructure.persistence.entities.component;

import jakarta.persistence.*;
import lombok.*;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;

@Entity
@Table(name = "product_component")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductComponentEntity {

    @EmbeddedId
    private ProductComponentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("componentId")
    @JoinColumn(name = "component_id")
    private ComponentEntity component;

    @Column(nullable = false)
    private Long quantity;
}