package org.example.mebkuch.application.usecase;

import lombok.RequiredArgsConstructor;

import org.example.mebkuch.api.dto.FavouriteDto;
import org.example.mebkuch.application.service.FavouriteRedisService;
import org.example.mebkuch.domain.models.user.FavouriteModel;
import org.example.mebkuch.domain.service.FavouriteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FavouriteQueryUseCase {

    private final FavouriteRedisService favouriteRedisService;
    private final FavouriteService favouriteService;

    public List<FavouriteDto> getUserFavourites(Long userId) {

        Set<Long> productIds = favouriteRedisService.getAll(userId);

        if (productIds.isEmpty()) {
            return favouriteService.getUserFavourites(userId)
                    .stream()
                    .map(this::mapToDto)
                    .toList();
        }

        return productIds.stream()
                .map(productId -> FavouriteDto.builder()
                        .userId(userId)
                        .productId(productId)
                        .build())
                .toList();
    }

    public FavouriteDto getOne(Long productId, Long userId) {

        boolean exists = favouriteRedisService.exists(userId, productId);

        if (!exists) {
            return favouriteService.getUserFavourites(userId)
                    .stream()
                    .filter(f -> f.getProductId().equals(productId))
                    .findFirst()
                    .map(this::mapToDto)
                    .orElseThrow(() -> new RuntimeException("Favourite not found"));
        }

        return FavouriteDto.builder()
                .userId(userId)
                .productId(productId)
                .build();
    }

    private FavouriteDto mapToDto(FavouriteModel model) {
        return FavouriteDto.builder()
                .productId(model.getProductId())
                .userId(model.getUserId())
                .addedDate(model.getAddedDate())
                .build();
    }
}