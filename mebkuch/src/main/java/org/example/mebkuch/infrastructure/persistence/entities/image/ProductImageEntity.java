package org.example.mebkuch.infrastructure.persistence.entities.image;

import jakarta.persistence.*;
import lombok.*;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;

@Entity
@Table(name = "product_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath;

    @Column(name = "is_main")
    private Boolean isMain;

    @Column(name = "sort_order")
    private Integer sortOrder;
}