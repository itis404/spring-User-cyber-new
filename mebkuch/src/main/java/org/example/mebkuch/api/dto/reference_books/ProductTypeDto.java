package org.example.mebkuch.api.dto.reference_books;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTypeDto {

    private Long id;
    private String name;
    private Boolean hasComponents;

}
