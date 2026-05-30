package org.example.mebkuch.infrastructure.persistence.entities.category;


import jakarta.persistence.*;

import java.io.Serializable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CategoryTreeId implements Serializable {

    @Column(name = "ancestor_id")
    private Long ancestorId;

    @Column(name = "descendant_id")
    private Long descendantId;
}