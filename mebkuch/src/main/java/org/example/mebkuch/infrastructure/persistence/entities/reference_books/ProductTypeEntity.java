package org.example.mebkuch.infrastructure.persistence.entities.reference_books;


import jakarta.persistence.*;

import lombok.*;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProductTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    @ToString.Include
    private String name;

    @Column(name = "has_components")
    @Builder.Default
    @ToString.Include
    private Boolean hasComponents = false;

    @OneToMany(mappedBy = "productTypeEntity", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductEntity> productEntityList = new ArrayList<>();

}