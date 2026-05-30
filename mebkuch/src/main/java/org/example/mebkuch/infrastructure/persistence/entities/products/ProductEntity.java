package org.example.mebkuch.infrastructure.persistence.entities.products;

import jakarta.persistence.*;
import lombok.*;
import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryEntity;
import org.example.mebkuch.infrastructure.persistence.entities.component.ProductComponentEntity;
import org.example.mebkuch.infrastructure.persistence.entities.eav.AttributeValueEntity;
import org.example.mebkuch.infrastructure.persistence.entities.image.ProductImageEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductSectionEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStatusEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStyleEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductTypeEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"sections"})
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "min_price", nullable = false)
    private BigDecimal minPrice;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity сategoryEntity;

    @ManyToOne
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductTypeEntity productTypeEntity;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private ProductStatusEntity productStatusEntity;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private ProductStyleEntity productStyleEntity;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "product_to_section",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "section_id")
    )
    private Set<ProductSectionEntity> sections = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "product_attribute_value",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_value_id")
    )
    private Set<AttributeValueEntity> attributeValues = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductComponentEntity> productComponentEntities = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImageEntity> images = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductSubProductEntity> subProducts = new HashSet<>();
}