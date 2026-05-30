package org.example.mebkuch.infrastructure.persistence.entities.eav;

import jakarta.persistence.*;
import lombok.*;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "attribute_value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private AttributeEntity attribute;

    @Column(name = "value_text")
    private String valueText;

    @Column(name = "value_number")
    private BigDecimal valueNumber;

    @Column(name = "value_boolean")
    private Boolean valueBoolean;

    @ManyToMany(mappedBy = "attributeValues")
    private Set<ProductEntity> products = new HashSet<>();
}
