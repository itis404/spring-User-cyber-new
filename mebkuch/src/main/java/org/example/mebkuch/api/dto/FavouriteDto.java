package org.example.mebkuch.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavouriteDto {

    private Long productId;
    private Long userId;
    private LocalDateTime addedDate;
}