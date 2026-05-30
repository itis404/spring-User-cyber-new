package org.example.mebkuch.infrastructure.persistence.entities.reference_books;


import jakarta.persistence.*;

import lombok.*;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProductStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    @ToString.Include
    private String name;

    @OneToMany(mappedBy = "productStatusEntity", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<ProductEntity> productEntityList = new ArrayList<>();
}
