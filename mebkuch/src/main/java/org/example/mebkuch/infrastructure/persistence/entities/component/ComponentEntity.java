package org.example.mebkuch.infrastructure.persistence.entities.component;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "component")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ComponentCategoryEntity category;

    @Column(length = 100)
    private String material;

    @Column(length = 100)
    private String country;

    @Column(precision = 10, scale = 0)
    private BigDecimal cost;

    @Builder.Default
    @OneToMany(mappedBy = "component")
    private Set<ProductComponentEntity> products = new HashSet<>();
}