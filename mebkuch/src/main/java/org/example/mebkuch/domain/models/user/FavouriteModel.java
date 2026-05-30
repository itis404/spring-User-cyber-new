package org.example.mebkuch.domain.models.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavouriteModel {

    private Long productId;
    private Long userId;
    private LocalDateTime addedDate;
}