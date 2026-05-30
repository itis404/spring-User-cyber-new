package org.example.mebkuch.infrastructure.persistence.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FavouriteId implements Serializable {
    private Long productId;
    private Long userId;
}
