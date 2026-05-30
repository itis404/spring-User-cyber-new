package org.example.mebkuch.domain.models.product;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductComponentModel {

    @EqualsAndHashCode.Include
    private Long productId;

    @EqualsAndHashCode.Include
    private Long componentId;

    private Long quantity;
}