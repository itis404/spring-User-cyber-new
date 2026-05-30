package org.example.mebkuch.api.dto.image;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductImageDto {
    @EqualsAndHashCode.Include
    private Long id;

    private Long productId;

    private String imagePath;

    private Boolean isMain;

    private Integer sortOrder;
}
