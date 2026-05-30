package org.example.mebkuch.infrastructure.persistence.entities.reference_books;

import jakarta.persistence.*;
import lombok.*;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_section")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductSectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    @ToString.Include
    private String name;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Builder.Default
    @ManyToMany(mappedBy = "sections")
    private Set<ProductEntity> products = new HashSet<>();
}