package org.example.mebkuch.infrastructure.persistence.entities.component;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductComponentId implements Serializable {

    private Long productId;
    private Long componentId;
}