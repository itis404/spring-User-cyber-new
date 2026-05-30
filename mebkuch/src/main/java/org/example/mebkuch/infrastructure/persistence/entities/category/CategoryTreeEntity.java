package org.example.mebkuch.infrastructure.persistence.entities.category;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category_tree")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CategoryTreeEntity {

    @EmbeddedId
    @EqualsAndHashCode.Include
    @ToString.Include
    private CategoryTreeId id;

    @Column(name = "depth", nullable = false)
    @ToString.Include
    private Integer depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ancestor_id", insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CategoryEntity ancestor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "descendant_id", insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CategoryEntity descendant;
}