package org.example.mebkuch.domain.models.reference_book;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStyleModel {
    private Long id;
    private String name;
    private String imageUrl;
}