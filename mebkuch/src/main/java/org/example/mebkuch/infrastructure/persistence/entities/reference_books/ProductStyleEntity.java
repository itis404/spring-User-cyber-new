package org.example.mebkuch.infrastructure.persistence.entities.reference_books;

import jakarta.persistence.*;

import lombok.*;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_style")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProductStyleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "name", unique = true, length = 50)
    @ToString.Include
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "productStyleEntity", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductEntity> productEntityList = new ArrayList<>();
}