package org.example.mebkuch.domain.models.reference_book;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductSubProductModel {

    @EqualsAndHashCode.Include
    private Long productId;

    @EqualsAndHashCode.Include
    private Long subProductId;
}