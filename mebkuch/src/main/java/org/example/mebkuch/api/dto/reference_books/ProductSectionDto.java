package org.example.mebkuch.api.dto.reference_books;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSectionDto {
    private Long id;
    private String name;
    private String imageUrl;
}
