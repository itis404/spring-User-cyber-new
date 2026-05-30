package org.example.mebkuch.infrastructure.persistence.entities.component;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "component_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}