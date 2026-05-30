package org.example.mebkuch.domain.models.image;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductImageModel {

    @EqualsAndHashCode.Include
    private Long id;

    private Long productId;

    private String imagePath;

    private Boolean isMain;

    private Integer sortOrder;
}